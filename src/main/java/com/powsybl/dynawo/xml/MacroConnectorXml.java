/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class MacroConnectorXml {

    private MacroConnectorXml() {
    }

    public static void writeMacroConnect(XMLStreamWriter writer, String connector, String id1, String id2) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", connector);
        writer.writeAttribute("id1", id1);
        writer.writeAttribute("id2", id2);
    }

    public static void writeConnect(XMLStreamWriter writer, String var1, String var2) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "connect");
        writer.writeAttribute("var1", var1);
        writer.writeAttribute("var2", var2);
    }

}
