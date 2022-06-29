/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import org.apache.commons.lang3.tuple.Pair;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.MACRO_CONNECTOR_PREFIX;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class MacroConnector {

    private final List<Pair<String, String>> varsConnect;
    private final String id;

    public MacroConnector(String lib1, String lib2, List<Pair<String, String>> varsConnect) {
        this.id = MACRO_CONNECTOR_PREFIX + lib1 + "-" + lib2;
        this.varsConnect = varsConnect;
    }

    public void writeMacroConnect(XMLStreamWriter writer, String id1, String id2) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", id);
        writer.writeAttribute("id1", id1);
        writer.writeAttribute("id2", id2);
    }

    public void writeMacroConnect(XMLStreamWriter writer, String id1, String id2, String name2) throws XMLStreamException {
        writeMacroConnect(writer, id1, id2);
        writer.writeAttribute("name2", name2);
    }

    public void write(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "macroConnector");
        writer.writeAttribute("id", id);
        for (Pair<String, String> varPair : this.varsConnect) {
            writeConnect(writer, varPair.getLeft(), varPair.getRight());
        }
        writer.writeEndElement();
    }

    public void writeMacroConnect(XMLStreamWriter writer, String id1, int index1, String id2) throws XMLStreamException {
        writeMacroConnect(writer, id1, index1, id2, null);
    }

    public void writeMacroConnect(XMLStreamWriter writer, String id1, int index1, String id2, String name2) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", id);
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
