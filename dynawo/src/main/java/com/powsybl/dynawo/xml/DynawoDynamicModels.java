/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
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

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoDynamicModels {

    private DynawoDynamicModels() {
    }

    public static void writeDynamicModels(XMLStreamWriter writer, List<DynawoDynamicModel> dynamicModels) {
        Objects.requireNonNull(writer);
        Objects.requireNonNull(dynamicModels);
        for (DynawoDynamicModel dynamicModel : dynamicModels) {
            writeDynamicModel(writer, dynamicModel);
        }
    }

    private static void writeDynamicModel(XMLStreamWriter writer, DynawoDynamicModel dynamicModel) {
        BiConsumer<XMLStreamWriter, DynawoDynamicModel> m = MODEL_WRITERS.get(dynamicModel.getClass());
        m.accept(writer, dynamicModel);
    }

    private static void writeBlackBoxModel(XMLStreamWriter writer, DynawoDynamicModel dynamicModel) {
        BlackBoxModel blackBoxModel = (BlackBoxModel) dynamicModel;
        List<StaticRef> staticRefs = blackBoxModel.getStaticRefs();
        List<DydComponent> macroStaticRefs = blackBoxModel.getMacroStaticRefs();
        try {
            if (staticRefs.isEmpty() && macroStaticRefs.isEmpty()) {
                writer.writeEmptyElement(DYN_URI, "blackBoxModel");
            } else {
                writer.writeStartElement(DYN_URI, "blackBoxModel");
            }
            writer.writeAttribute("id", blackBoxModel.getId());
            writer.writeAttribute("lib", blackBoxModel.getLib());
            writer.writeAttribute("parFile", blackBoxModel.getParametersFile());
            writer.writeAttribute("parId", blackBoxModel.getParametersId());
            String staticId = blackBoxModel.getStaticId();
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
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeModelicaModel(XMLStreamWriter writer, DynawoDynamicModel dynamicModel) {
        ModelicaModel modelicaModel = (ModelicaModel) dynamicModel;
        try {
            String id = modelicaModel.getId();
            String staticId = modelicaModel.getStaticId();
            writer.writeStartElement(DYN_URI, "modelicaModel");
            writer.writeAttribute("id", id);
            if (staticId != null) {
                writer.writeAttribute("staticId", staticId);
            }
            for (UnitDynamicModel unitDynamicModel : modelicaModel.getUnitDynamicModels()) {
                writeUnitDynamicModel(writer, unitDynamicModel);
            }
            for (Connection connection : modelicaModel.getConnections()) {
                writeConnection(writer, connection);
            }
            for (InitConnection initConnection : modelicaModel.getInitConnections()) {
                writeInitConnection(writer, initConnection);
            }
            for (StaticRef staticRef : modelicaModel.getStaticRefs()) {
                writeStaticRef(writer, staticRef);
            }
            for (DydComponent macroStaticRef : modelicaModel.getMacroStaticRefs()) {
                writeMacroStaticRef(writer, macroStaticRef);
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeModelTemplate(XMLStreamWriter writer, DynawoDynamicModel dynamicModel) {
        ModelTemplate modelTemplate = (ModelTemplate) dynamicModel;
        try {
            writer.writeStartElement(DYN_URI, "modelTemplate");
            writer.writeAttribute("id", modelTemplate.getId());
            for (UnitDynamicModel unitDynamicModel : modelTemplate.getUnitDynamicModels()) {
                writeUnitDynamicModel(writer, unitDynamicModel);
            }
            for (Connection connection : modelTemplate.getConnections()) {
                writeConnection(writer, connection);
            }
            for (InitConnection initConnection : modelTemplate.getInitConnections()) {
                writeInitConnection(writer, initConnection);
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeModelTemplateExpansion(XMLStreamWriter writer, DynawoDynamicModel dynamicModel) {
        ModelTemplateExpansion modelTemplateExpansion = (ModelTemplateExpansion) dynamicModel;
        try {
            writer.writeEmptyElement(DYN_URI, "modelTemplateExpansion");
            writer.writeAttribute("id", modelTemplateExpansion.getId());
            writer.writeAttribute("templateId", modelTemplateExpansion.getTemplateId());
            writer.writeAttribute("parFile", modelTemplateExpansion.getParametersFile());
            writer.writeAttribute("parId", modelTemplateExpansion.getParametersId());
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeUnitDynamicModel(XMLStreamWriter writer, UnitDynamicModel dynamicModel) {
        try {
            writer.writeEmptyElement(DYN_URI, "unitDynamicModel");
            writer.writeAttribute("id", dynamicModel.getId());
            writer.writeAttribute("name", dynamicModel.getName());
            String initName = dynamicModel.getInitName();
            if (initName != null) {
                writer.writeAttribute("initName", initName);
            }
            String parFile = dynamicModel.getParametersFile();
            if (parFile != null) {
                writer.writeAttribute("parFile", parFile);
                writer.writeAttribute("parId", dynamicModel.getParametersId());
            }
            String moFile = dynamicModel.getMoFile();
            if (moFile != null) {
                writer.writeAttribute("moFile", moFile);
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeConnection(XMLStreamWriter writer, DynawoDynamicModel dynamicModel) {
        Connection connection = (Connection) dynamicModel;
        try {
            writer.writeEmptyElement(DYN_URI, "connect");
            writer.writeAttribute("id1", connection.getId1());
            writer.writeAttribute("var1", connection.getVar1());
            writer.writeAttribute("id2", connection.getId2());
            writer.writeAttribute("var2", connection.getVar2());
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeInitConnection(XMLStreamWriter writer, DynawoDynamicModel dynamicModel) {
        InitConnection connection = (InitConnection) dynamicModel;
        try {
            writer.writeEmptyElement(DYN_URI, "initConnect");
            writer.writeAttribute("id1", connection.getId1());
            writer.writeAttribute("var1", connection.getVar1());
            writer.writeAttribute("id2", connection.getId2());
            writer.writeAttribute("var2", connection.getVar2());
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeMacroConnector(XMLStreamWriter writer, DynawoDynamicModel dynamicModel) {
        MacroConnector connector = (MacroConnector) dynamicModel;
        try {
            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", connector.getId());
            for (DydConnection connection : connector.getConnections()) {
                writeMacroConnectorConnection(writer, connection);
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeMacroConnection(XMLStreamWriter writer, DynawoDynamicModel dynamicModel) {
        MacroConnection connection = (MacroConnection) dynamicModel;
        try {
            writer.writeEmptyElement(DYN_URI, "macroConnect");
            writer.writeAttribute("connector", connection.getConnector());
            writer.writeAttribute("id1", connection.getId1());
            writer.writeAttribute("id2", connection.getId2());
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeMacroConnectorConnection(XMLStreamWriter writer, DydConnection dynamicModel)
        throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "connect");
        writer.writeAttribute("var1", dynamicModel.getVar1());
        writer.writeAttribute("var2", dynamicModel.getVar2());
    }

    private static void writeMacroStaticReference(XMLStreamWriter writer, DynawoDynamicModel dynamicModel) {
        MacroStaticReference macroStaticReference = (MacroStaticReference) dynamicModel;
        try {
            writer.writeStartElement(DYN_URI, "macroStaticReference");
            writer.writeAttribute("id", macroStaticReference.getId());
            for (StaticRef staticRef : macroStaticReference.getStaticRefs()) {
                writeStaticRef(writer, staticRef);
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeStaticRef(XMLStreamWriter writer, StaticRef dynamicModel) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "staticRef");
        writer.writeAttribute("var", dynamicModel.getVar());
        writer.writeAttribute("staticVar", dynamicModel.getStaticVar());
    }

    private static void writeMacroStaticRef(XMLStreamWriter writer, DydComponent macroStaticRef)
        throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroStaticRef");
        writer.writeAttribute("id", macroStaticRef.getId());
    }

    private static final Map<Class, BiConsumer<XMLStreamWriter, DynawoDynamicModel>> MODEL_WRITERS = new HashMap<>();

    static {
        MODEL_WRITERS.put(BlackBoxModel.class, DynawoDynamicModels::writeBlackBoxModel);
        MODEL_WRITERS.put(ModelicaModel.class, DynawoDynamicModels::writeModelicaModel);
        MODEL_WRITERS.put(ModelTemplate.class, DynawoDynamicModels::writeModelTemplate);
        MODEL_WRITERS.put(ModelTemplateExpansion.class, DynawoDynamicModels::writeModelTemplateExpansion);
        MODEL_WRITERS.put(Connection.class, DynawoDynamicModels::writeConnection);
        MODEL_WRITERS.put(InitConnection.class, DynawoDynamicModels::writeInitConnection);
        MODEL_WRITERS.put(MacroConnector.class, DynawoDynamicModels::writeMacroConnector);
        MODEL_WRITERS.put(MacroConnection.class, DynawoDynamicModels::writeMacroConnection);
        MODEL_WRITERS.put(MacroStaticReference.class, DynawoDynamicModels::writeMacroStaticReference);
    }
}
