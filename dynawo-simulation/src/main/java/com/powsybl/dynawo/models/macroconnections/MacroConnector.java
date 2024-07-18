/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.macroconnections;

import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.utils.SideUtils;
import com.powsybl.iidm.network.TwoSides;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_URI;
import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.MACRO_CONNECTOR_PREFIX;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
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

    public static String createMacroConnectorId(String name1, String name2, TwoSides side) {
        return MACRO_CONNECTOR_PREFIX + name1 + SideUtils.getSideSuffix(side) + "-" + name2;
    }

    public static String createMacroConnectorId(String name1, String name2, String name1Suffix) {
        return MACRO_CONNECTOR_PREFIX + name1 + name1Suffix + "-" + name2;
    }

    public void write(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "macroConnector");
        writer.writeAttribute("id", id);
        for (VarConnection varConnection : this.varConnections) {
            writer.writeEmptyElement(DYN_URI, "connect");
            writer.writeAttribute("var1", varConnection.var1());
            writer.writeAttribute("var2", varConnection.var2());
        }
        writer.writeEndElement();
    }
}
