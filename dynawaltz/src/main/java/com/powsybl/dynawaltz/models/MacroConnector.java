/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.dynawaltz.models.utils.Couple;
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

    private final Pair<BlackBoxModel, Model> connectedBbms;
    private final List<VarConnection> varConnections;
    private final String id;

    public MacroConnector(BlackBoxModel bbm, Model model, List<VarConnection> varConnections) {
        this.connectedBbms = Pair.of(bbm, model);
        this.id = MACRO_CONNECTOR_PREFIX + bbm.getLib() + "-" + model.getName();
        this.varConnections = varConnections;
    }

    public Pair<BlackBoxModel, Model> getConnectedBbms() {
        return this.connectedBbms;
    }

    public List<VarConnection> getVarsConnections() {
        return this.varConnections;
    }

    public Couple<String> getLibCouple() {
        return new Couple<>(connectedBbms.getLeft().getLib(), connectedBbms.getRight().getName());
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
        for (VarConnection varConnection : this.varConnections) {
            writer.writeEmptyElement(DYN_URI, "connect");
            writer.writeAttribute("var1", varConnection.getVar1());
            writer.writeAttribute("var2", varConnection.getVar2());
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
        return id.hashCode() * varConnections.hashCode() * connectedBbms.hashCode();
    }
}
