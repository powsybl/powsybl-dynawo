/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.MACRO_CONNECTOR_PREFIX;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.MACRO_STATIC_REFERENCE_PREFIX;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.NETWORK;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;
import com.powsybl.dynawaltz.xml.MacroConnectorXml;
import com.powsybl.dynawaltz.xml.MacroStaticReferenceXml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractGeneratorModel extends AbstractBlackBoxModel {

    public AbstractGeneratorModel(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        if (context.getIndex(getLib(), true) == 0) {
            // Write the macroStaticReference object
            writer.writeStartElement(DYN_URI, "macroStaticReference");
            writer.writeAttribute("id", MACRO_STATIC_REFERENCE_PREFIX + getLib());
            MacroStaticReferenceXml.writeStaticRef(writer, "generator_PGenPu", "p");
            MacroStaticReferenceXml.writeStaticRef(writer, "generator_QGenPu", "q");
            MacroStaticReferenceXml.writeStaticRef(writer, "generator_state", "state");
            writer.writeEndElement();

            // Write the macroConnector object
            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib());
            MacroConnectorXml.writeConnect(writer, "generator_terminal", "@STATIC_ID@@NODE@_ACPIN");
            MacroConnectorXml.writeConnect(writer, "generator_switchOffSignal1", "@STATIC_ID@@NODE@_switchOff");
            writer.writeEndElement();
        }

        writeBlackBoxModel(writer, context);

        // Write the connect object
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib(), getDynamicModelId(), NETWORK);
    }
}
