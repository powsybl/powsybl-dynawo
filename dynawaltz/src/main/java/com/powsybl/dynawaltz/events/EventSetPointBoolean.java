/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.events;

import static com.powsybl.dynawaltz.xml.DynawaltzXmlConstants.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawaltz.xml.DynawaltzXmlContext;
import com.powsybl.dynawaltz.xml.MacroConnectorXml;

/**
 * @author Mathieu BAGUE {@literal <mathieu.bague at rte-france.com>}
 */
public class EventSetPointBoolean extends AbstractBlackBoxEventModel {

    public EventSetPointBoolean(String eventModelId, String staticId, String parameterSetId) {
        super(eventModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "EventSetPointBoolean";
    }

    @Override
    public void write(XMLStreamWriter writer, DynawaltzXmlContext context) throws XMLStreamException {
        if (context.getIndex(getLib(), true) == 0) {
            // Write the macroConnector object
            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib());
            MacroConnectorXml.writeConnect(writer, "event_state1", "generator_switchOffSignal2");
            writer.writeEndElement();
        }

        writeEventBlackBoxModel(writer, context);

        // Write the connect object
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib(), getEventModelId(), getStaticId());
    }
}
