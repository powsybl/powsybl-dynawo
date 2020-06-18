/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import static com.powsybl.dynawo.xml.DynawoConstants.DYD_FILENAME;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.dyd.AbstractBlackBoxModel;
import com.powsybl.dynawo.dyd.AbstractDynawoDynamicModel;
import com.powsybl.dynawo.dyd.MacroConnect;
import com.powsybl.dynawo.dyd.MacroConnector;
import com.powsybl.dynawo.dyd.MacroConnector.Connect;
import com.powsybl.dynawo.dyd.MacroStaticReference;
import com.powsybl.dynawo.dyd.MacroStaticReference.StaticRef;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynamicModelsXml {

    private DynamicModelsXml() {
    }

    public static void write(Path workingDir, DynawoContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(DYD_FILENAME);

        XmlUtil.write(file, context, "dynamicModelsArchitecture", DynamicModelsXml::write);
    }

    private static void write(XMLStreamWriter writer, DynawoContext context) {
        DydXmlWriterContext dydXmlWriterContext = new DydXmlWriterContext(context);
        try {
            // First write all dynamic models
            for (DynamicModel dynamicModel : context.getDynamicModels()) {
                // All dynamic models must be Dynawo abstract dynamic models
                AbstractDynawoDynamicModel dynawoDynamicModel = (AbstractDynawoDynamicModel) dynamicModel;
                writeDynamicModel(writer, dydXmlWriterContext, dynawoDynamicModel);
            }

            // Write all macroConnects related to the dynamic models
            // We need to loop again for all dynamic models
            for (DynamicModel dynamicModel : context.getDynamicModels()) {
                AbstractDynawoDynamicModel dynDynamicModel = (AbstractDynawoDynamicModel) dynamicModel;
                for (MacroConnect macroConnect : dynDynamicModel.getMacroConnects()) {
                    writeMacroConnect(writer, dydXmlWriterContext, macroConnect);
                }
            }

            // Write macro... objects referenced by the dynamic models written
            for (String macroConnectorId : dydXmlWriterContext.macroConnectorsUsed) {
                writeMacroConnector(writer, context.getMacroConnector(macroConnectorId));
            }
            for (String macroStaticReferenceId : dydXmlWriterContext.macroStaticReferencesUsed) {
                writeMacroStaticReference(writer, context.getMacroStaticReference(macroStaticReferenceId));
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeDynamicModel(XMLStreamWriter writer, DydXmlWriterContext dydXmlWriterContext,
            AbstractDynawoDynamicModel dynawoDynamicModel) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, TAG_NAMES.get(dynawoDynamicModel.getType()));
        writer.writeAttribute("id", dynawoDynamicModel.getId());
        writeDynamicModelAttributes(writer, dydXmlWriterContext, dynawoDynamicModel);

        for (String macroStaticRef : dynawoDynamicModel.getMacroStaticRefs()) {
            writer.writeEmptyElement(DYN_URI, "macroStaticRef");
            writer.writeAttribute("id", macroStaticRef);
            dydXmlWriterContext.macroStaticReferencesUsed.add(macroStaticRef);
        }
        writer.writeEndElement();
    }

    private static void writeDynamicModelAttributes(XMLStreamWriter writer, DydXmlWriterContext dydXmlWriterContext,
            AbstractDynawoDynamicModel dynawoDynamicModel) throws XMLStreamException {
        // TODO only black box models for the moment
        if (dynawoDynamicModel instanceof AbstractBlackBoxModel) {
            AbstractBlackBoxModel blackBox = (AbstractBlackBoxModel) dynawoDynamicModel;
            writer.writeAttribute("lib", blackBox.getLib());
            writer.writeAttribute("parFile", dydXmlWriterContext.parFile);
            writer.writeAttribute("parId", blackBox.getParameterSetId());
            writer.writeAttribute("staticId", blackBox.getStaticId());
        }
    }

    private static void writeMacroConnect(XMLStreamWriter writer, DydXmlWriterContext dydXmlWriterContext,
            MacroConnect macroConnect) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", macroConnect.getId());
        writer.writeAttribute("id1", macroConnect.getId1());
        writer.writeAttribute("id2", macroConnect.getId2());
        dydXmlWriterContext.macroConnectorsUsed.add(macroConnect.getId());
    }

    private static void writeMacroConnector(XMLStreamWriter writer, MacroConnector macroConnector)
            throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "macroConnector");
        writer.writeAttribute("id", macroConnector.getId());
        for (Connect connect : macroConnector.getConnections()) {
            writer.writeEmptyElement(DYN_URI, "connect");
            writer.writeAttribute("var1", connect.getVar1());
            writer.writeAttribute("var2", connect.getVar2());
        }
        writer.writeEndElement();
    }

    private static void writeMacroStaticReference(XMLStreamWriter writer, MacroStaticReference macroStaticReference)
            throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "macroStaticReference");
        writer.writeAttribute("id", macroStaticReference.getId());
        for (StaticRef staticRef : macroStaticReference.getStaticRefs()) {
            writer.writeEmptyElement(DYN_URI, "staticRef");
            writer.writeAttribute("var", staticRef.getVar());
            writer.writeAttribute("staticVar", staticRef.getStaticVar());
        }
        writer.writeEndElement();
    }

    private static class DydXmlWriterContext {
        DydXmlWriterContext(DynawoContext context) {
            parametersFile = Paths.get(context.getDynawoParameters().getParametersFile());
            parFile = parametersFile.getFileName().toString();
        }

        final Path parametersFile;
        final String parFile;
        final Set<String> macroConnectorsUsed = new HashSet<>();
        final Set<String> macroStaticReferencesUsed = new HashSet<>();
    }

    private static final EnumMap<AbstractDynawoDynamicModel.DynamicModelType, String> TAG_NAMES = new EnumMap<>(
            AbstractDynawoDynamicModel.DynamicModelType.class);

    static {
        TAG_NAMES.put(AbstractDynawoDynamicModel.DynamicModelType.MODELICA_MODEL, "modelicaModel");
        TAG_NAMES.put(AbstractDynawoDynamicModel.DynamicModelType.BLACK_BOX_MODEL, "blackBoxModel");
    }
}
