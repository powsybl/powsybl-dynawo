/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.events;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.MACRO_CONNECTOR_PREFIX;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.NETWORK;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;
import com.powsybl.dynawaltz.xml.MacroConnectorXml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class EventQuadripoleDisconnection extends AbstractBlackBoxEventModel {

    public EventQuadripoleDisconnection(String eventModelId, String staticId, String parameterSetId) {
        super(eventModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "EventQuadripoleDisconnection";
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        if (context.getIndex(getLib(), true) == 0) {
            // Write the macroConnector object
            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib());
            MacroConnectorXml.writeConnect(writer, "event_state1_value", "@NAME@_state_value");
            writer.writeEndElement();
        }

        writeEventBlackBoxModel(writer, context);

        // Write the connect object
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib(), getEventModelId(), NETWORK, getStaticId());
    }
}
