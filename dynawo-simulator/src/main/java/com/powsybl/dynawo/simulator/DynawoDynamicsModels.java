/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

public class DynawoDynamicsModels {

    public DynawoDynamicsModels(Network network, DynawoConfig config) {
        this.network = network;
        this.config = config;
    }

    public void prepareFile() {
        Path parFile = config.getWorkingDir().resolve("dynawoModel.dyd");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), dynamicsModels()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CharSequence dynamicsModels() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            "<?xml version='1.0' encoding='UTF-8'?>",
            "<!--",
            "    Copyright (c) 2015-2019, RTE (http://www.rte-france.com)",
            "    See AUTHORS.txt",
            "    All rights reserved.",
            "    This Source Code Form is subject to the terms of the Mozilla Public",
            "    License, v. 2.0. If a copy of the MPL was not distributed with this",
            "    file, you can obtain one at http://mozilla.org/MPL/2.0/.",
            "    SPDX-License-Identifier: MPL-2.0",
            "",
            "    This file is part of Dynawo, an hybrid C++/Modelica open source time domain",
            "    simulation tool for power systems.",
            "-->",
            "<dyn:dynamicModelsArchitecture xmlns:dyn=\"http://www.rte-france.com/dynawo\">",
            System.lineSeparator()));
        int id = 2;
        for (Load l : network.getLoads()) {
            loadDynamicsModels(l, builder, id++);
        }
        for (Generator g : network.getGenerators()) {
            genDynamicsModels(g, builder, id++);
        }
        omegaRefDynamicsModels(builder, id++);
        eventDynamicsModels(builder, id++);
        for (Load l : network.getLoads()) {
            loadConnections(l, builder);
        }
        int grp = 0;
        for (Generator g : network.getGenerators()) {
            genConnections(g, builder, grp++);
        }
        builder.append(String.join(System.lineSeparator(),
            "</dyn:dynamicModelsArchitecture>",
            System.lineSeparator()));
        return builder.toString();
    }

    private void omegaRefDynamicsModels(StringBuilder builder, int id) {
        builder.append(String.join(System.lineSeparator(),
            "  <dyn:blackBoxModel id=\"OMEGA_REF\" lib=\"DYNModelOmegaRef\" parFile=\"dynawoModel.par\" parId=\"" + id
                + "\" />",
                System.lineSeparator()));
    }

    private void eventDynamicsModels(StringBuilder builder, int id) {
        builder.append(String.join(System.lineSeparator(),
            "  <dyn:blackBoxModel id=\"DISCONNCET_LINE\" lib=\"EventQuadripoleDisconnection\" parFile=\"dynawoModel.par\" parId=\""
                + id + "\" />",
                System.lineSeparator()));
    }

    private void loadDynamicsModels(Load l, StringBuilder builder, int id) {
        builder.append(String.join(System.lineSeparator(),
            "  <dyn:blackBoxModel id=\"" + l.getId() + "\" lib=\"LoadAlphaBeta\" parFile=\"dynawoModel.par\" parId=\""
                + id + "\" staticId=\"" + l.getId() + "\" />",
                System.lineSeparator()));
    }

    private void genDynamicsModels(Generator g, StringBuilder builder, int id) {
        builder.append(String.join(System.lineSeparator(),
            "  <dyn:blackBoxModel id=\"" + g.getId()
                + "\" lib=\"GeneratorSynchronousFourWindingsProportionalRegulations\" parFile=\"dynawoModel.par\" parId=\""
                + id + "\" staticId=\"" + g.getId() + "\" />",
                System.lineSeparator()));
    }

    private void loadConnections(Load l, StringBuilder builder) {
        builder.append(String.join(System.lineSeparator(),
            "  <dyn:connect id1=\"" + l.getId() + "\" var1=\"load_terminal\" id2=\"NETWORK\" var2=\""
                + l.getTerminal().getBusBreakerView().getBus().getId() + "_ACPIN\"/>",
                System.lineSeparator()));
    }

    private void genConnections(Generator g, StringBuilder builder, int grp) {
        builder.append(String.join(System.lineSeparator(),
            "  <dyn:connect id1=\"OMEGA_REF\" var1=\"omega_grp_" + grp + "\" id2=\"" + g.getId()
                + "\" var2=\"generator_omegaPu\"/>",
            "  <dyn:connect id1=\"OMEGA_REF\" var1=\"omegaRef_grp_" + grp + "\" id2=\"" + g.getId()
                + "\" var2=\"generator_omegaRefPu\"/>",
            "  <dyn:connect id1=\"OMEGA_REF\" var1=\"numcc_node_" + grp + "\" id2=\"NETWORK\" var2=\"@" + g.getId()
                + "@@NODE@_numcc\"/>",
            "  <dyn:connect id1=\"OMEGA_REF\" var1=\"running_grp_" + grp + "\" id2=\"" + g.getId()
                + "\" var2=\"generator_running\"/>",
            "  <dyn:connect id1=\"" + g.getId() + "\" var1=\"generator_terminal\" id2=\"NETWORK\" var2=\"@" + g.getId()
                + "@@NODE@_ACPIN\"/>",
            "  <dyn:connect id1=\"" + g.getId() + "\" var1=\"generator_switchOffSignal1\" id2=\"NETWORK\" var2=\"@"
                + g.getId() + "@@NODE@_switchOff\"/>",
                System.lineSeparator()));
    }

    private final Network network;
    private final DynawoConfig config;
}
