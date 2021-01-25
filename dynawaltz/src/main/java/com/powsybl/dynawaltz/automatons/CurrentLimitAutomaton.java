/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.automatons;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawaltz.xml.DynawaltzXmlContext;
import com.powsybl.dynawaltz.xml.MacroConnectorXml;
import com.powsybl.dynawaltz.dynamicmodels.AbstractBlackBoxModel;
import com.powsybl.iidm.network.Branch;

import static com.powsybl.dynawaltz.xml.DynawaltzXmlConstants.DYN_URI;
import static com.powsybl.dynawaltz.xml.DynawaltzXmlConstants.MACRO_CONNECTOR_PREFIX;
import static com.powsybl.dynawaltz.xml.DynawaltzXmlConstants.NETWORK;

import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class CurrentLimitAutomaton extends AbstractBlackBoxModel {

    private final Branch.Side side;

    public CurrentLimitAutomaton(String dynamicModelId, String staticId, String parameterSetId, Branch.Side side) {
        super(dynamicModelId, staticId, parameterSetId);
        this.side = Objects.requireNonNull(side);
    }

    @Override
    public String getLib() {
        return "CurrentLimitAutomaton";
    }

    @Override
    public void write(XMLStreamWriter writer, DynawaltzXmlContext context) throws XMLStreamException {
        String postfix = getPostfix(side);
        if (context.getIndex(getLib(), true) == 0) {
            // Write the macroConnector object

            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib() + "ToLine" + postfix);
            MacroConnectorXml.writeConnect(writer, "currentLimitAutomaton_IMonitored", "@NAME@_i" + postfix);
            MacroConnectorXml.writeConnect(writer, "currentLimitAutomaton_order", "@NAME@_state");
            MacroConnectorXml.writeConnect(writer, "currentLimitAutomaton_AutomatonExists", "@NAME@_desactivate_currentLimits");
            writer.writeEndElement();
        }

        writeAutomatonBlackBoxModel(writer, context);

        // Write the connect object
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib() + "ToLine" + postfix, getDynamicModelId(), NETWORK, getStaticId());
    }

    private static String getPostfix(Branch.Side side) {
        switch (side) {
            case ONE:
                return "Side1";
            case TWO:
                return "Side2";
            default:
                throw new AssertionError("Unexpected Side value: " + side);
        }
    }
}
