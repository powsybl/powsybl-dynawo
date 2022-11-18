/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.MacroConnector;
import com.powsybl.dynawaltz.dynamicmodels.nonstaticref.events.BlackBoxEventModel;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.DYD_FILENAME;

/**
 * @author Mathieu Bague <mathieu.bague@rte-france.com>
 */
public final class DydXml {

    private DydXml() {
    }

    public static void write(Path workingDir, DynaWaltzContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(DYD_FILENAME);

        XmlUtil.write(file, context, "dynamicModelsArchitecture", DydXml::write);
    }

    private static void write(XMLStreamWriter writer, DynaWaltzContext context) {
        writeDynamicModels(writer, context);
        writeEvents(writer, context);
    }

    private static void writeDynamicModels(XMLStreamWriter writer, DynaWaltzContext context) {

        try {
            // loop over the values of the map indexed by dynamicIds to write only once objects with the same dynamicId
            for (BlackBoxModel model : context.getBlackBoxModels()) {
                model.write(writer, context);
            }
            for (MacroConnector macroConnector : context.getMacroConnectors()) {
                macroConnector.write(writer);
            }
            for (MacroStaticReference macroStaticReference : context.getMacroStaticReferences()) {
                macroStaticReference.write(writer);
            }
            for (Map.Entry<BlackBoxModel, List<BlackBoxModel>> bbmMapping : context.getModelsConnections().entrySet()) {
                BlackBoxModel bbm = bbmMapping.getKey();
                for (BlackBoxModel connectedBbm : bbmMapping.getValue()) {
                    bbm.writeMacroConnect(writer, context, context.getMacroConnector(bbm, connectedBbm), connectedBbm);
                }
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeEvents(XMLStreamWriter writer, DynaWaltzContext context) {

        try {
            for (BlackBoxEventModel model : context.getBlackBoxEventModels()) {
                model.write(writer, context);
            }
            for (MacroConnector macroConnector : context.getEventMacroConnectors()) {
                macroConnector.write(writer);
            }
            for (Map.Entry<BlackBoxEventModel, List<BlackBoxModel>> bbemMapping : context.getEventModelsConnections().entrySet()) {
                BlackBoxEventModel bbem = bbemMapping.getKey();
                for (BlackBoxModel connectedBbm : bbemMapping.getValue()) {
                    bbem.writeMacroConnect(writer, context, context.getEventMacroConnector(bbem, connectedBbm), connectedBbm);
                }
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}
