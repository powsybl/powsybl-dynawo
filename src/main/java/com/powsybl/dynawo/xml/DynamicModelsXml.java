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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.dyd.DynawoDynamicModel;
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
        List<String> macroConnectorsNeeded = new ArrayList<>();
        List<String> macroStaticReferencesNeeded = new ArrayList<>();
        Path parametersFile = Paths.get(context.getDynawoParameters().getParametersFile());
        try {
            for (DynamicModel dynamicModel : context.getDynamicModels()) {
                DynawoDynamicModel dynDynamicModel = (DynawoDynamicModel) dynamicModel;
                writer.writeStartElement(DYN_URI, "blackBoxModel");
                writer.writeAttribute("id", dynDynamicModel.getId());
                writer.writeAttribute("lib", dynDynamicModel.getLib());
                writer.writeAttribute("parFile", parametersFile.getFileName().toString());
                writer.writeAttribute("parId", dynDynamicModel.getParameterSetId());
                writer.writeAttribute("staticId", dynDynamicModel.getStaticId());
                for (String macroStaticRef : dynDynamicModel.getMacroStaticRefs()) {
                    writer.writeEmptyElement(DYN_URI, "macroStaticRef");
                    writer.writeAttribute("id", macroStaticRef);
                    if (!macroStaticReferencesNeeded.contains(macroStaticRef)) {
                        macroStaticReferencesNeeded.add(macroStaticRef);
                    }
                }
                writer.writeEndElement();
                for (MacroConnect macroConnect : dynDynamicModel.getMacroConnects()) {
                    writer.writeEmptyElement(DYN_URI, "macroConnect");
                    writer.writeAttribute("connector", macroConnect.getId());
                    writer.writeAttribute("id1", macroConnect.getId1());
                    writer.writeAttribute("id2", macroConnect.getId2());
                    if (!macroConnectorsNeeded.contains(macroConnect.getId())) {
                        macroConnectorsNeeded.add(macroConnect.getId());
                    }
                }
            }

            for (String macroConnectorId : macroConnectorsNeeded) {
                MacroConnector macroConnector = context.getMacroConnector(macroConnectorId);
                writer.writeStartElement(DYN_URI, "macroConnector");
                writer.writeAttribute("id", macroConnector.getId());
                for (Connect connect : macroConnector.getConnections()) {
                    writer.writeEmptyElement(DYN_URI, "connect");
                    writer.writeAttribute("var1", connect.getVar1());
                    writer.writeAttribute("var2", connect.getVar2());
                }
                writer.writeEndElement();
            }

            for (String macroStaticReferenceId : macroStaticReferencesNeeded) {
                MacroStaticReference macroStaticReference = context.getMacroStaticReference(macroStaticReferenceId);
                writer.writeStartElement(DYN_URI, "macroStaticReference");
                writer.writeAttribute("id", macroStaticReference.getId());
                for (StaticRef staticRef : macroStaticReference.getStaticRefs()) {
                    writer.writeEmptyElement(DYN_URI, "staticRef");
                    writer.writeAttribute("var", staticRef.getVar());
                    writer.writeAttribute("staticVar", staticRef.getStaticVar());
                }
                writer.writeEndElement();
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}
