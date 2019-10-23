/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.DynawoParameterType;
import com.powsybl.dynawo.dyd.BlackBoxModel;
import com.powsybl.dynawo.dyd.Connection;
import com.powsybl.dynawo.dyd.DydComponent;
import com.powsybl.dynawo.dyd.DydConnection;
import com.powsybl.dynawo.dyd.DynawoDynamicModel;
import com.powsybl.dynawo.dyd.InitConnection;
import com.powsybl.dynawo.dyd.MacroConnection;
import com.powsybl.dynawo.dyd.MacroConnector;
import com.powsybl.dynawo.dyd.MacroStaticReference;
import com.powsybl.dynawo.dyd.ModelTemplate;
import com.powsybl.dynawo.dyd.ModelicaModel;
import com.powsybl.dynawo.dyd.StaticRef;
import com.powsybl.dynawo.dyd.UnitDynamicModel;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;
import static java.lang.Math.toIntExact;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoDynamicModels {

    private DynawoDynamicModels() {
    }

    public static void writeDynamicModels(XMLStreamWriter writer, List<DynawoDynamicModel> dynamicModels)
        throws XMLStreamException {
        for (DynawoDynamicModel dynamicModel : dynamicModels) {
            writeDynamicModel(writer, dynamicModel);
        }
    }

    public static int countGeneratorConnections(List<DynawoDynamicModel> dynamicModels) {
        return toIntExact(dynamicModels.stream().filter(dynamicModel -> Connection.class.isInstance(dynamicModel)
            && ((Connection) dynamicModel).getVar2().equals("generator_omegaPu")).count());
    }

    public static boolean definedDynamicModel(List<DynawoDynamicModel> dynamicModels, String id) {
        return dynamicModels.stream()
            .anyMatch(dynamicModel -> dynamicModel.getId() != null && dynamicModel.getId().equals(id));
    }

    public static void writeDefaultOmegaRef(XMLStreamWriter writer, int parId) throws XMLStreamException {
        writeDynamicModel(writer,
            new BlackBoxModel(DynawoParameterType.OMEGA_REF.getValue(), "DYNModelOmegaRef",
                DynawoParameterType.DYNAWO_PAR.getValue(), parId));
    }

    public static void writeDefaultLoad(XMLStreamWriter writer, Load load, int parId) throws XMLStreamException {
        writeDefaultLoadBlackBoxModel(writer, load, parId);
        writeDefaultLoadConnection(writer, load);
    }

    private static void writeDefaultLoadBlackBoxModel(XMLStreamWriter writer, Load load, int parId)
        throws XMLStreamException {
        writeDynamicModel(writer,
            new BlackBoxModel(load.getId(), "LoadAlphaBeta", DynawoParameterType.DYNAWO_PAR.getValue(), parId,
                load.getId()));
    }

    private static void writeDefaultLoadConnection(XMLStreamWriter writer, Load load) throws XMLStreamException {
        if (load.getTerminal().getBusBreakerView() != null) {
            writeDynamicModel(writer,
                new Connection(load.getId(), "load_terminal", DynawoParameterType.NETWORK.getValue(),
                    load.getTerminal().getBusBreakerView().getBus().getId() + "_ACPIN"));
        } else {
            writeDynamicModel(writer,
                new Connection(load.getId(), "load_terminal", DynawoParameterType.NETWORK.getValue(),
                    load.getTerminal().getBusView().getBus().getId() + "_ACPIN"));
        }
    }

    public static void writeDefaultGenerator(XMLStreamWriter writer, Generator generator, int parId, int id)
        throws XMLStreamException {
        writeDefaultGeneratorBlackBoxModel(writer, generator, parId);
        writeDefaultGeneratorConnection(writer, generator, id);
    }

    private static void writeDefaultGeneratorBlackBoxModel(XMLStreamWriter writer, Generator generator, int parId)
        throws XMLStreamException {
        writeDynamicModel(writer, new BlackBoxModel(generator.getId(),
            "GeneratorSynchronousFourWindingsProportionalRegulations", DynawoParameterType.DYNAWO_PAR.getValue(),
            parId, generator.getId()));
    }

    private static void writeDefaultGeneratorConnection(XMLStreamWriter writer, Generator generator, int id)
        throws XMLStreamException {
        writeDynamicModel(writer,
            new Connection(DynawoParameterType.OMEGA_REF.getValue(), "omega_grp_" + id, generator.getId(),
                "generator_omegaPu"));
        writeDynamicModel(writer, new Connection(DynawoParameterType.OMEGA_REF.getValue(), "omegaRef_grp_" + id,
            generator.getId(),
            "generator_omegaRefPu"));
        writeDynamicModel(writer, new Connection(DynawoParameterType.OMEGA_REF.getValue(), "numcc_node_" + id,
            DynawoParameterType.NETWORK.getValue(),
            "@" + generator.getId() + "@@NODE@_numcc"));
        writeDynamicModel(writer,
            new Connection(DynawoParameterType.OMEGA_REF.getValue(), "running_grp_" + id, generator.getId(),
                "generator_running"));
        writeDynamicModel(writer,
            new Connection(generator.getId(), "generator_terminal", DynawoParameterType.NETWORK.getValue(),
                "@" + generator.getId() + "@@NODE@_ACPIN"));
        writeDynamicModel(writer,
            new Connection(generator.getId(), "generator_switchOffSignal1", DynawoParameterType.NETWORK.getValue(),
                "@" + generator.getId() + "@@NODE@_switchOff"));
    }

    private static void writeDynamicModel(XMLStreamWriter writer, DynawoDynamicModel dynamicModel)
        throws XMLStreamException {
        if (BlackBoxModel.class.isInstance(dynamicModel)) {
            writeBlackBoxModel(writer, (BlackBoxModel) dynamicModel);
        } else if (ModelicaModel.class.isInstance(dynamicModel)) {
            writeModelicaModel(writer, (ModelicaModel) dynamicModel);
        } else if (ModelTemplate.class.isInstance(dynamicModel)) {
            writeModelTemplate(writer, (ModelTemplate) dynamicModel);
        } else if (Connection.class.isInstance(dynamicModel)) {
            writeConnection(writer, (Connection) dynamicModel);
        } else if (InitConnection.class.isInstance(dynamicModel)) {
            writeInitConnection(writer, (InitConnection) dynamicModel);
        } else if (MacroConnector.class.isInstance(dynamicModel)) {
            writeMacroConnector(writer, (MacroConnector) dynamicModel);
        } else if (MacroConnection.class.isInstance(dynamicModel)) {
            writeMacroConnection(writer, (MacroConnection) dynamicModel);
        } else if (MacroStaticReference.class.isInstance(dynamicModel)) {
            writeMacroStaticReference(writer, (MacroStaticReference) dynamicModel);
        } else {
            throw new PowsyblException("DynamicModel class " + dynamicModel.getClass().getName() + " is not allowed");
        }
    }

    private static void writeBlackBoxModel(XMLStreamWriter writer, BlackBoxModel dynamicModel)
        throws XMLStreamException {
        String id = dynamicModel.getId();
        String lib = dynamicModel.getLib();
        String file = dynamicModel.getParametersFile();
        int paramId = dynamicModel.getParametersId();
        String staticId = dynamicModel.getStaticId();
        List<StaticRef> staticRefs = dynamicModel.getStaticRefs();
        List<DydComponent> macroStaticRefs = dynamicModel.getMacroStaticRefs();
        writeBlackBoxModel(writer, id, lib, file, paramId, staticId, staticRefs, macroStaticRefs);
    }

    private static void writeModelicaModel(XMLStreamWriter writer, ModelicaModel dynamicModel)
        throws XMLStreamException {
        String id = dynamicModel.getId();
        String staticId = dynamicModel.getStaticId();
        writer.writeStartElement(DYN_URI, "modelicaModel");
        writer.writeAttribute("id", id);
        writer.writeAttribute("staticId", staticId);
        for (UnitDynamicModel unitDynamicModel : dynamicModel.getUnitDynamicModels()) {
            writeUnitDynamicModel(writer, unitDynamicModel);
        }
        for (Connection connection : dynamicModel.getConnections()) {
            writeConnection(writer, connection);
        }
        for (InitConnection initConnection : dynamicModel.getInitConnections()) {
            writeInitConnection(writer, initConnection);
        }
        for (StaticRef staticRef : dynamicModel.getStaticRefs()) {
            writeStaticRef(writer, staticRef);
        }
        for (DydComponent macroStaticRef : dynamicModel.getMacroStaticRefs()) {
            writeMacroStaticRef(writer, macroStaticRef);
        }
        writer.writeEndElement();
    }

    private static void writeModelTemplate(XMLStreamWriter writer, ModelTemplate dynamicModel)
        throws XMLStreamException {
        String id = dynamicModel.getId();
        writer.writeStartElement(DYN_URI, "modelTemplate");
        writer.writeAttribute("id", id);
        for (UnitDynamicModel unitDynamicModel : dynamicModel.getUnitDynamicModels()) {
            writeUnitDynamicModel(writer, unitDynamicModel);
        }
        for (Connection connection : dynamicModel.getConnections()) {
            writeConnection(writer, connection);
        }
        for (InitConnection initConnection : dynamicModel.getInitConnections()) {
            writeInitConnection(writer, initConnection);
        }
        writer.writeEndElement();
    }

    private static void writeUnitDynamicModel(XMLStreamWriter writer, UnitDynamicModel dynamicModel)
        throws XMLStreamException {
        String id = dynamicModel.getId();
        String name = dynamicModel.getName();
        String moFile = dynamicModel.getMoFile();
        String initName = dynamicModel.getInitName();
        String parFile = dynamicModel.getParametersFile();
        int parId = dynamicModel.getParametersId();
        writeUnitDynamicModel(writer, id, name, moFile, initName, parFile, parId);
    }

    private static void writeConnection(XMLStreamWriter writer, Connection dynamicModel) throws XMLStreamException {
        String id1 = dynamicModel.getId1();
        String var1 = dynamicModel.getVar1();
        String id2 = dynamicModel.getId2();
        String var2 = dynamicModel.getVar2();
        writeConnection(writer, id1, var1, id2, var2);
    }

    private static void writeInitConnection(XMLStreamWriter writer, InitConnection dynamicModel)
        throws XMLStreamException {
        String id1 = dynamicModel.getId1();
        String var1 = dynamicModel.getVar1();
        String id2 = dynamicModel.getId2();
        String var2 = dynamicModel.getVar2();
        writeInitConnection(writer, id1, var1, id2, var2);
    }

    private static void writeMacroConnector(XMLStreamWriter writer, MacroConnector dynamicModel)
        throws XMLStreamException {
        String id = dynamicModel.getId();
        writer.writeStartElement(DYN_URI, "macroConnector");
        writer.writeAttribute("id", id);
        for (DydConnection connection : dynamicModel.getConnections()) {
            writeConnection(writer, connection);
        }
        writer.writeEndElement();
    }

    private static void writeMacroConnection(XMLStreamWriter writer, MacroConnection dynamicModel)
        throws XMLStreamException {
        String connector = dynamicModel.getConnector();
        String id1 = dynamicModel.getId1();
        String id2 = dynamicModel.getId2();
        writeMacroConnection(writer, connector, id1, id2);
    }

    private static void writeConnection(XMLStreamWriter writer, DydConnection dynamicModel) throws XMLStreamException {
        String var1 = dynamicModel.getVar1();
        String var2 = dynamicModel.getVar2();
        writeConnection(writer, var1, var2);
    }

    private static void writeMacroStaticReference(XMLStreamWriter writer, MacroStaticReference dynamicModel)
        throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "macroStaticReference");
        for (StaticRef staticRef : dynamicModel.getStaticRefs()) {
            writeStaticRef(writer, staticRef);
        }
        writer.writeEndElement();
    }

    private static void writeStaticRef(XMLStreamWriter writer, StaticRef dynamicModel) throws XMLStreamException {
        String var = dynamicModel.getVar();
        String staticVar = dynamicModel.getStaticVar();
        writeStaticRef(writer, var, staticVar);
    }

    private static void writeBlackBoxModel(XMLStreamWriter writer, String id, String lib, String parFile, int parId,
        String staticId, List<StaticRef> staticRefs, List<DydComponent> macroStaticRefs) throws XMLStreamException {
        if (staticRefs.isEmpty() && macroStaticRefs.isEmpty()) {
            writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        } else {
            writer.writeStartElement(DYN_URI, "blackBoxModel");
        }
        writer.writeAttribute("id", id);
        writer.writeAttribute("lib", lib);
        writer.writeAttribute("parFile", parFile);
        writer.writeAttribute("parId", Integer.toString(parId));
        if (staticId != null) {
            writer.writeAttribute("staticId", staticId);
        }
        if (!staticRefs.isEmpty() || !macroStaticRefs.isEmpty()) {
            for (StaticRef staticRef : staticRefs) {
                writeStaticRef(writer, staticRef);
            }
            for (DydComponent macroStaticRef : macroStaticRefs) {
                writeMacroStaticRef(writer, macroStaticRef);
            }
            writer.writeEndElement();
        }
    }

    private static void writeUnitDynamicModel(XMLStreamWriter writer, String id, String name, String moFile,
        String initName, String parFile, int parId) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "unitDynamicModel");
        writer.writeAttribute("id", id);
        writer.writeAttribute("name", name);
        writer.writeAttribute("initName", initName);
        writer.writeAttribute("parFile", parFile);
        writer.writeAttribute("parId", Integer.toString(parId));
        if (moFile != null) {
            writer.writeAttribute("moFile", moFile);
        }
    }

    private static void writeMacroStaticRef(XMLStreamWriter writer, DydComponent macroStaticRef)
        throws XMLStreamException {
        String id = macroStaticRef.getId();
        writeMacroStaticRef(writer, id);
    }

    private static void writeConnection(XMLStreamWriter writer, String id1, String var1, String id2, String var2)
        throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "connect");
        if (id1 != null) {
            writer.writeAttribute("id1", id1);
        }
        writer.writeAttribute("var1", var1);
        if (id2 != null) {
            writer.writeAttribute("id2", id2);
        }
        writer.writeAttribute("var2", var2);
    }

    private static void writeInitConnection(XMLStreamWriter writer, String id1, String var1, String id2, String var2)
        throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "initConnect");
        writer.writeAttribute("id1", id1);
        writer.writeAttribute("var1", var1);
        writer.writeAttribute("id2", id2);
        writer.writeAttribute("var2", var2);
    }

    private static void writeMacroConnection(XMLStreamWriter writer, String connector, String id1, String id2)
        throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", connector);
        writer.writeAttribute("id1", id1);
        writer.writeAttribute("id2", id2);
    }

    private static void writeConnection(XMLStreamWriter writer, String var1, String var2) throws XMLStreamException {
        writeConnection(writer, null, var1, null, var2);
    }

    private static void writeStaticRef(XMLStreamWriter writer, String var, String staticVar) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "staticRef");
        writer.writeAttribute("var", var);
        writer.writeAttribute("staticVar", staticVar);
    }

    private static void writeMacroStaticRef(XMLStreamWriter writer, String id) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroStaticRef");
        writer.writeAttribute("id", id);
    }
}
