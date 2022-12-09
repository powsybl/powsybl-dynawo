/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels.utils;

import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
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

    private final Pair<BlackBoxModel, BlackBoxModel> connectedBbms;
    private final List<Pair<String, String>> varsConnect;
    private final String id;

    public MacroConnector(BlackBoxModel bbm1, BlackBoxModel bbm2, List<Pair<String, String>> varsConnect) {
        this.connectedBbms = Pair.of(bbm1, bbm2);
        this.id = MACRO_CONNECTOR_PREFIX + bbm1.getLib() + "-" + bbm2.getLib();
        this.varsConnect = varsConnect;
    }

    public Pair<BlackBoxModel, BlackBoxModel> getConnectedBbms() {
        return this.connectedBbms;
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
            if (mcObj.getConnectedBbms().getLeft().equals(getConnectedBbms().getLeft()) && mcObj.getConnectedBbms().getRight().equals(getConnectedBbms().getRight())
                || mcObj.getConnectedBbms().getLeft().equals(getConnectedBbms().getRight()) && mcObj.getConnectedBbms().getRight().equals(getConnectedBbms().getLeft())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode() * varsConnect.hashCode() * connectedBbms.hashCode();
    }
}
