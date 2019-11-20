/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.input;

import static com.powsybl.dynawo.simulator.DynawoConstants.PAR_FILENAME;
import static com.powsybl.dynawo.simulator.DynawoXmlConstants.DYN_URI;

import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Load;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoDynamicsModels {

    private static final String NETWORK = "NETWORK";
    private static final String OMEGA_REF = "OMEGA_REF";

    private DynawoDynamicsModels() {
        throw new IllegalStateException("Utility class");
    }

    public static void writeOmegaRefDynamicsModels(XMLStreamWriter writer, int id) throws XMLStreamException {
        writeBlackBoxModel(writer, OMEGA_REF, "DYNModelOmegaRef", id);
    }

    public static void writeEventDisconnectLineDynamicsModels(XMLStreamWriter writer, int id)
        throws XMLStreamException {
        writeBlackBoxModel(writer, "DISCONNECT_LINE", "EventQuadripoleDisconnection", id);
    }

    public static void writeLoadDynamicsModels(Load l, XMLStreamWriter writer, int id) throws XMLStreamException {
        writeBlackBoxModel(writer, l.getId(), "LoadAlphaBeta", id, l.getId());
    }

    public static void writeGeneratorDynamicsModels(Generator g, XMLStreamWriter writer, int id)
        throws XMLStreamException {
        writeBlackBoxModel(writer, g.getId(), "GeneratorSynchronousFourWindingsProportionalRegulations", id,
            g.getId());
    }

    public static void writeLoadConnections(Load l, XMLStreamWriter writer) throws XMLStreamException {
        wrtieConnection(writer, l.getId(), "load_terminal", NETWORK,
            l.getTerminal().getBusBreakerView().getBus().getId() + "_ACPIN");
    }

    public static void writeGeneratorConnections(Generator g, XMLStreamWriter writer, int grp)
        throws XMLStreamException {
        wrtieConnection(writer, OMEGA_REF, "omega_grp_" + grp, g.getId(), "generator_omegaPu");
        wrtieConnection(writer, OMEGA_REF, "omegaRef_grp_" + grp, g.getId(), "generator_omegaRefPu");
        wrtieConnection(writer, OMEGA_REF, "numcc_node_" + grp, NETWORK, "@" + g.getId() + "@@NODE@_numcc");
        wrtieConnection(writer, OMEGA_REF, "running_grp_" + grp, g.getId(), "generator_running");
        wrtieConnection(writer, g.getId(), "generator_terminal", NETWORK, "@" + g.getId() + "@@NODE@_ACPIN");
        wrtieConnection(writer, g.getId(), "generator_switchOffSignal1", NETWORK,
            "@" + g.getId() + "@@NODE@_switchOff");
    }

    public static void writeEventDisconnectLineConnections(Line l, XMLStreamWriter writer) throws XMLStreamException {
        Objects.requireNonNull(l);
        wrtieConnection(writer, "DISCONNECT_LINE", "event_state1_value", NETWORK, l.getId() + "_state_value");
    }

    private static void writeBlackBoxModel(XMLStreamWriter writer, String id, String lib, int parId)
        throws XMLStreamException {
        writeBlackBoxModel(writer, id, lib, parId, null);
    }

    private static void writeBlackBoxModel(XMLStreamWriter writer, String id, String lib, int parId, String staticId)
        throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", id);
        writer.writeAttribute("lib", lib);
        writer.writeAttribute("parFile", PAR_FILENAME);
        writer.writeAttribute("parId", Integer.toString(parId));
        if (staticId != null) {
            writer.writeAttribute("staticId", staticId);
        }
    }

    private static void wrtieConnection(XMLStreamWriter writer, String id1, String var1, String id2, String var2)
        throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "connect");
        writer.writeAttribute("id1", id1);
        writer.writeAttribute("var1", var1);
        writer.writeAttribute("id2", id2);
        writer.writeAttribute("var2", var2);
    }
}
