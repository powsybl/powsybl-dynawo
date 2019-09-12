/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.input;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoDynamicsModels {

    private static final String NETWORK = "NETWORK";
    private static final String OMEGA_REF = "OMEGA_REF";

    public DynawoDynamicsModels(Network n) {
        this.n = n;
    }

    public void prepareFile(Path workingDir) {
        Path parFile = workingDir.resolve("dynawoModel.dyd");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), dynamicsModels()));
        } catch (IOException e) {
            LOGGER.error("Error in file dynawoModel.dyd");
        }
    }

    private CharSequence dynamicsModels() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            DynawoInput.setInputHeader(),
            "<dyn:dynamicModelsArchitecture xmlns:dyn=\"http://www.rte-france.com/dynawo\">") + System.lineSeparator());
        int id = 2;
        for (Load l : n.getLoads()) {
            loadDynamicsModels(l, builder, id++);
        }
        for (Generator g : n.getGenerators()) {
            genDynamicsModels(g, builder, id++);
        }
        omegaRefDynamicsModels(builder, id++);
        eventDisconnectLineDynamicsModels(builder, id);
        for (Load l : n.getLoads()) {
            loadConnections(l, builder);
        }
        int grp = 0;
        for (Generator g : n.getGenerators()) {
            genConnections(g, builder, grp++);
        }
        eventDisconnectLineConnections(n.getLineStream().findFirst().get(), builder);
        builder.append(String.join(System.lineSeparator(),
            "</dyn:dynamicModelsArchitecture>") + System.lineSeparator());
        return builder.toString();
    }

    private void omegaRefDynamicsModels(StringBuilder builder, int id) {
        builder.append(String.join(System.lineSeparator(),
            setBlackBoxModel(OMEGA_REF, "DYNModelOmegaRef", id))
            + System.lineSeparator());
    }

    private void eventDisconnectLineDynamicsModels(StringBuilder builder, int id) {
        builder.append(String.join(System.lineSeparator(),
            setBlackBoxModel("DISCONNECT_LINE", "EventQuadripoleDisconnection", id))
            + System.lineSeparator());
    }

    private void loadDynamicsModels(Load l, StringBuilder builder, int id) {
        builder.append(String.join(System.lineSeparator(),
            setBlackBoxModel(l.getId(), "LoadAlphaBeta", id, l.getId()))
            + System.lineSeparator());
    }

    private void genDynamicsModels(Generator g, StringBuilder builder, int id) {
        builder.append(String.join(System.lineSeparator(),
            setBlackBoxModel(g.getId(), "GeneratorSynchronousFourWindingsProportionalRegulations", id, g.getId()))
            + System.lineSeparator());
    }

    private void loadConnections(Load l, StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            setConnection(l.getId(), "load_terminal", NETWORK,
                l.getTerminal().getBusBreakerView().getBus().getId() + "_ACPIN"))
            + System.lineSeparator());
    }

    private void genConnections(Generator g, StringBuilder builder, int grp) {
        builder.append(String.join(System.lineSeparator(),
            setConnection(OMEGA_REF, "omega_grp_" + grp, g.getId(), "generator_omegaPu"),
            setConnection(OMEGA_REF, "omegaRef_grp_" + grp, g.getId(), "generator_omegaRefPu"),
            setConnection(OMEGA_REF, "numcc_node_" + grp, NETWORK, "@" + g.getId() + "@@NODE@_numcc"),
            setConnection(OMEGA_REF, "running_grp_" + grp, g.getId(), "generator_running"),
            setConnection(g.getId(), "generator_terminal", NETWORK, "@" + g.getId() + "@@NODE@_ACPIN"),
            setConnection(g.getId(), "generator_switchOffSignal1", NETWORK, "@" + g.getId() + "@@NODE@_switchOff"))
            + System.lineSeparator());
    }

    private void eventDisconnectLineConnections(Line l, StringBuilder builder) {
        Objects.requireNonNull(l);
        builder.append(String.join(System.lineSeparator(),
            setConnection("DISCONNECT_LINE", "event_state1_value", "NETWORK", l.getId() + "_state_value"))
            + System.lineSeparator());
    }

    private String setBlackBoxModel(String id, String lib, int parId) {
        return "  <dyn:blackBoxModel id=\"" + id + "\" lib=\"" + lib + "\" parFile=\"dynawoModel.par\" parId=\"" + parId
            + "\" />";
    }

    private String setBlackBoxModel(String id, String lib, int parId, String staticId) {
        return "  <dyn:blackBoxModel id=\"" + id + "\" lib=\"" + lib + "\" parFile=\"dynawoModel.par\" parId=\"" + parId
            + "\" staticId=\"" + staticId + "\" />";
    }

    private String setConnection(String id1, String var1, String id2, String var2) {
        return "  <dyn:connect id1=\"" + id1 + "\" var1=\"" + var1 + "\" id2=\"" + id2 + "\" var2=\"" + var2 + "\"/>";
    }

    private final Network n;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoDynamicsModels.class);
}
