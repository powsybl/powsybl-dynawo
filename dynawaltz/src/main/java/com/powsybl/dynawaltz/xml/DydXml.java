/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.MacroConnector;
import com.powsybl.dynawaltz.events.AbstractBlackBoxEventModel;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
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
        DynaWaltzXmlContext xmlContext = new DynaWaltzXmlContext(context);

        try {
            context.computeModelsConnectedTo();
            context.computeMacroConnectors();
            for (MacroConnector macroConnector : context.getMacroConnectors()) {
                macroConnector.write(writer);
            }
            for (MacroStaticReference macroStaticReference : context.getMacroStaticReferences()) {
                macroStaticReference.write(writer);
            }
            for (BlackBoxModel model : context.getBlackBoxModels()) {
                model.write(writer, xmlContext);
            }
            for (Map.Entry<BlackBoxModel, BlackBoxModel> bbmMapping : context.getModelsConnections().entrySet()) {
                bbmMapping.getKey().writeMacroConnect(writer, bbmMapping.getValue());
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeEvents(XMLStreamWriter writer, DynaWaltzContext context) {
        DynaWaltzXmlContext xmlContext = new DynaWaltzXmlContext(context);

        try {
            for (EventModel model : context.getEventModels()) {
                AbstractBlackBoxEventModel dynawoModel = (AbstractBlackBoxEventModel) model;
                dynawoModel.write(writer, xmlContext);
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}
