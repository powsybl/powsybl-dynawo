/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.automatons;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.MACRO_CONNECTOR_PREFIX;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.NETWORK;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.dynamicmodels.AbstractBlackBoxModel;
import com.powsybl.dynawo.xml.DynawoXmlContext;
import com.powsybl.dynawo.xml.MacroConnectorXml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class CurrentLimitAutomaton extends AbstractBlackBoxModel {

    private static final String TO_LINE_SIDE1 = "ToLineSide1";
    private static final String TO_LINE_SIDE2 = "ToLineSide2";

    public CurrentLimitAutomaton(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "CurrentLimitAutomaton";
    }

    @Override
    public void write(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException {
        if (context.getIndex(getLib(), true) == 0) {
            // Write the macroConnector object
            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib() + TO_LINE_SIDE1);
            MacroConnectorXml.writeConnect(writer, "currentLimitAutomaton_IMonitored", "@NAME@_iSide1");
            MacroConnectorXml.writeConnect(writer, "currentLimitAutomaton_order", "@NAME@_state");
            MacroConnectorXml.writeConnect(writer, "currentLimitAutomaton_AutomatonExists", "@NAME@_desactivate_currentLimits");
            writer.writeEndElement();

            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib() + TO_LINE_SIDE2);
            MacroConnectorXml.writeConnect(writer, "currentLimitAutomaton_IMonitored", "@NAME@_iSide2");
            MacroConnectorXml.writeConnect(writer, "currentLimitAutomaton_order", "@NAME@_state");
            MacroConnectorXml.writeConnect(writer, "currentLimitAutomaton_AutomatonExists", "@NAME@_desactivate_currentLimits");
            writer.writeEndElement();
        }

        writeAutomatonBlackBoxModel(writer, context);

        // Write the connect object
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib() + TO_LINE_SIDE1, getDynamicModelId(), NETWORK, getStaticId());
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib() + TO_LINE_SIDE2, getDynamicModelId(), NETWORK, getStaticId());
    }
}
