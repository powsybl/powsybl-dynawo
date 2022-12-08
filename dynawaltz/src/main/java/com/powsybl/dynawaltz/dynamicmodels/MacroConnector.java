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

    private final Pair<String, String> libs;
    private final List<Pair<String, String>> varsConnect;
    private final String id;

    public MacroConnector(String lib1, String lib2, List<Pair<String, String>> varsConnect) {
        this.libs = Pair.of(lib1, lib2);
        this.id = MACRO_CONNECTOR_PREFIX + lib1 + "-" + lib2;
        this.varsConnect = varsConnect;
    }

    public Pair<String, String> getLibs() {
        return this.libs;
    }

    public List<Pair<String, String>> getVarsConnect() {
        return this.varsConnect;
    }

    public void writeMacroConnect(XMLStreamWriter writer,
                                  List<Pair<String, String>> attributesFrom,
                                  List<Pair<String, String>> attributesTo) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", id);
        for (Pair<String, String> attribute : attributesFrom) {
            writer.writeAttribute(attribute.getKey(), attribute.getValue());
        }
        for (Pair<String, String> attribute : attributesTo) {
            writer.writeAttribute(attribute.getKey(), attribute.getValue());
        }
    }

    public void write(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "macroConnector");
        writer.writeAttribute("id", id);
        for (Pair<String, String> varPair : this.varsConnect) {
            writer.writeEmptyElement(DYN_URI, "connect");
            writer.writeAttribute("var1", varPair.getLeft());
            writer.writeAttribute("var2", varPair.getRight());
        }
        writer.writeEndElement();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MacroConnector) {
            MacroConnector mcObj = (MacroConnector) obj;
            if ((libs.getRight().equals(mcObj.getLibs().getRight()) || libs.getLeft().equals(mcObj.getLibs().getRight()))
                && (libs.getRight().equals(mcObj.getLibs().getLeft()) || libs.getLeft().equals(mcObj.getLibs().getLeft()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode() * varsConnect.hashCode() * libs.hashCode();
    }
}
