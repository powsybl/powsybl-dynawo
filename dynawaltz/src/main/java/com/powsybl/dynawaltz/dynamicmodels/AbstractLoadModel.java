/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;
import com.powsybl.dynawaltz.xml.MacroConnectorXml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractLoadModel extends AbstractBlackBoxModel {

    public AbstractLoadModel(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        if (context.getIndex(getLib(), true) == 0) {
            // Write the macroConnector object
            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib());
            writeConnector(writer, context);
            writer.writeEndElement();
        }

        writeBlackBoxModel(writer, context);

        // Write the connect object
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib(), getDynamicModelId(), NETWORK);
    }

    protected abstract void writeConnector(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException;
}
