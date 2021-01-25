/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import static com.powsybl.dynawaltz.xml.DynawaltzXmlConstants.DYN_URI;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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

    public static void writeMacroConnect(XMLStreamWriter writer, String connector, String id1, String id2, String name2) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", connector);
        writer.writeAttribute("id1", id1);
        writer.writeAttribute("id2", id2);
        writer.writeAttribute("name2", name2);
    }

    public static void writeMacroConnect(XMLStreamWriter writer, String connector, String id1, int index1, String id2) throws XMLStreamException {
        writeMacroConnect(writer, connector, id1, index1, id2, null);
    }

    public static void writeMacroConnect(XMLStreamWriter writer, String connector, String id1, int index1, String id2, String name2) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", connector);
        writer.writeAttribute("id1", id1);
        writer.writeAttribute("index1", Integer.toString(index1));
        writer.writeAttribute("id2", id2);
        if (name2 != null) {
            writer.writeAttribute("name2", name2);
        }
    }

    public static void writeConnect(XMLStreamWriter writer, String var1, String var2) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "connect");
        writer.writeAttribute("var1", var1);
        writer.writeAttribute("var2", var2);
    }

}
