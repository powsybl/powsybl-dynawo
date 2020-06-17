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
import com.powsybl.dynawo.dyd.MacroConnector;

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
                for (String macroStaticRefenceId : dynDynamicModel.getMacroStaticReferencesId()) {
                    writer.writeEmptyElement(DYN_URI, "macroStaticRef");
                    writer.writeAttribute("id", macroStaticRefenceId);
                    if (!macroStaticReferencesNeeded.contains(macroStaticRefenceId)) {
                        macroStaticReferencesNeeded.add(macroStaticRefenceId);
                    }
                }
                writer.writeEndElement();
                for (MacroConnector macroConnector : dynDynamicModel.getMacroConnectors()) {
                    writer.writeEmptyElement(DYN_URI, "macroConnect");
                    writer.writeAttribute("connector", macroConnector.getId());
                    writer.writeAttribute("id1", macroConnector.getId1());
                    writer.writeAttribute("id2", macroConnector.getId2());
                    if (!macroConnectorsNeeded.contains(macroConnector.getId())) {
                        macroConnectorsNeeded.add(macroConnector.getId());
                    }
                }
            }

            for (String macroConnectorId : macroConnectorsNeeded) {
                writer.writeCharacters(context.getMacroConnector(macroConnectorId));
            }

            for (String macroStaticRefenceId : macroStaticReferencesNeeded) {
                writer.writeCharacters(context.getMacroStaticReference(macroStaticRefenceId));
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}
