/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.MACRO_CONNECTOR_PREFIX;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class MacroConnector {

    private final List<VarConnection> varConnections;
    private final String id;

    public MacroConnector(String id, List<VarConnection> varConnections) {
        this.id = id;
        this.varConnections = varConnections;
    }

    public static String createMacroConnectorId(String name1) {
        return MACRO_CONNECTOR_PREFIX + name1;
    }

    public static String createMacroConnectorId(String name1, String name2) {
        return MACRO_CONNECTOR_PREFIX + name1 + "-" + name2;
    }

    public static String createMacroConnectorId(String name1, String name2, String parametrizedName) {
        return MACRO_CONNECTOR_PREFIX + name1 + parametrizedName + "-" + name2;
    }

    public void write(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "macroConnector");
        writer.writeAttribute("id", id);
        for (VarConnection varConnection : this.varConnections) {
            writer.writeEmptyElement(DYN_URI, "connect");
            writer.writeAttribute("var1", varConnection.getVar1());
            writer.writeAttribute("var2", varConnection.getVar2());
        }
        writer.writeEndElement();
    }
}
