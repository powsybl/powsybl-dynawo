/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.commons.PowsyblException;
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
import com.powsybl.dynawo.dyd.ModelTemplateExpansion;
import com.powsybl.dynawo.dyd.ModelicaModel;
import com.powsybl.dynawo.dyd.StaticRef;
import com.powsybl.dynawo.dyd.UnitDynamicModel;
import com.powsybl.iidm.network.Injection;

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

    private static void writeDynamicModel(XMLStreamWriter writer, DynawoDynamicModel dynamicModel)
        throws XMLStreamException {
        if (BlackBoxModel.class.isInstance(dynamicModel)) {
            writeBlackBoxModel(writer, (BlackBoxModel) dynamicModel);
        } else if (ModelicaModel.class.isInstance(dynamicModel)) {
            writeModelicaModel(writer, (ModelicaModel) dynamicModel);
        } else if (ModelTemplate.class.isInstance(dynamicModel)) {
            writeModelTemplate(writer, (ModelTemplate) dynamicModel);
        } else if (ModelTemplateExpansion.class.isInstance(dynamicModel)) {
            writeModelTemplateExpansion(writer, (ModelTemplateExpansion) dynamicModel);
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
        String paramId = dynamicModel.getParametersId();
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
        if (staticId != null) {
            writer.writeAttribute("staticId", staticId);
        }
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

    private static void writeModelTemplateExpansion(XMLStreamWriter writer, ModelTemplateExpansion dynamicModel)
        throws XMLStreamException {
        String id = dynamicModel.getId();
        String templateId = dynamicModel.getTemplateId();
        String file = dynamicModel.getParametersFile();
        String paramId = dynamicModel.getParametersId();
        writer.writeEmptyElement(DYN_URI, "modelTemplateExpansion");
        writer.writeAttribute("id", id);
        writer.writeAttribute("templateId", templateId);
        writer.writeAttribute("parFile", file);
        writer.writeAttribute("parId", paramId);
    }

    private static void writeUnitDynamicModel(XMLStreamWriter writer, UnitDynamicModel dynamicModel)
        throws XMLStreamException {
        String id = dynamicModel.getId();
        String name = dynamicModel.getName();
        String moFile = dynamicModel.getMoFile();
        String initName = dynamicModel.getInitName();
        String parFile = dynamicModel.getParametersFile();
        String parId = dynamicModel.getParametersId();
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
        String id = dynamicModel.getId();
        writer.writeStartElement(DYN_URI, "macroStaticReference");
        writer.writeAttribute("id", id);
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

    private static void writeBlackBoxModel(XMLStreamWriter writer, String id, String lib, String parFile, String parId,
        String staticId, List<StaticRef> staticRefs, List<DydComponent> macroStaticRefs) throws XMLStreamException {
        if (staticRefs.isEmpty() && macroStaticRefs.isEmpty()) {
            writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        } else {
            writer.writeStartElement(DYN_URI, "blackBoxModel");
        }
        writer.writeAttribute("id", id);
        writer.writeAttribute("lib", lib);
        writer.writeAttribute("parFile", parFile);
        writer.writeAttribute("parId", parId);
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
        String initName, String parFile, String parId) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "unitDynamicModel");
        writer.writeAttribute("id", id);
        writer.writeAttribute("name", name);
        if (initName != null) {
            writer.writeAttribute("initName", initName);
        }
        if (parFile != null) {
            writer.writeAttribute("parFile", parFile);
            writer.writeAttribute("parId", parId);
        }
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

    private static String translate(String token, Injection<?> injection) {
        return token.replace("#ID#", injection.getId()).replace("#IDBUS#",
            injection.getTerminal().getBusBreakerView() != null ? injection.getTerminal().getBusBreakerView().getBus().getId()
                : injection.getTerminal().getBusView().getBus().getId());
    }

    private static String translate(String token, int id) {
        return token.replace("#NUM#", Integer.toString(id));
    }
}
