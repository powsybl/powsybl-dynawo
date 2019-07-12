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

import com.powsybl.iidm.network.Network;

public class DynawoSolverParameters {

    public DynawoSolverParameters(Network network, DynawoConfig config) {
        this.network = network;
        this.config = config;
    }

    public void prepareFile() {
        Path parFile = config.getWorkingDir().resolve("solvers.par");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), parameters()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private CharSequence parameters() {
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
            "<parametersSet xmlns=\"http://www.rte-france.com/dynawo\">",
            "  <!-- IDA order 1 solver-->",
            "  <set id=\"1\">",
            "    <par type=\"INT\" name=\"order\" value=\"1\"/>",
            "    <par type=\"DOUBLE\" name=\"initStep\" value=\"0.000001\"/>",
            "    <par type=\"DOUBLE\" name=\"minStep\" value=\"0.000001\"/>",
            "    <par type=\"DOUBLE\" name=\"maxStep\" value=\"10\"/>",
            "    <par type=\"DOUBLE\" name=\"absAccuracy\" value=\"1e-4\"/>",
            "    <par type=\"DOUBLE\" name=\"relAccuracy\" value=\"1e-4\"/>",
            "  </set>",
            "  <!-- IDA order 2 solver -->",
            "  <set id=\"2\">",
            "    <par type=\"INT\" name=\"order\" value=\"2\"/>",
            "    <par type=\"DOUBLE\" name=\"initStep\" value=\"0.000001\"/>",
            "    <par type=\"DOUBLE\" name=\"minStep\" value=\"0.000001\"/>",
            "    <par type=\"DOUBLE\" name=\"maxStep\" value=\"10\"/>",
            "    <par type=\"DOUBLE\" name=\"absAccuracy\" value=\"1e-4\"/>",
            "    <par type=\"DOUBLE\" name=\"relAccuracy\" value=\"1e-4\"/>",
            "  </set>",
            "  <!-- Simplified solver without step recalculation -->",
            "  <set id=\"3\">",
            "    <par type=\"DOUBLE\" name=\"hMin\" value=\"0.000001\"/>",
            "    <par type=\"DOUBLE\" name=\"hMax\" value=\"1\"/>",
            "    <par type=\"DOUBLE\" name=\"kReduceStep\" value=\"0.5\"/>",
            "    <par type=\"INT\" name=\"nEff\" value=\"10\"/>",
            "    <par type=\"INT\" name=\"nDeadband\" value=\"2\"/>",
            "    <par type=\"INT\" name=\"maxRootRestart\" value=\"3\"/>",
            "    <par type=\"INT\" name=\"maxNewtonTry\" value=\"10\"/>",
            "    <par type=\"STRING\" name=\"linearSolverName\" value=\"KLU\"/>",
            "    <par type=\"BOOL\" name=\"recalculateStep\" value=\"false\"/>",
            "  </set>",
            "  <!-- Simplified solver with step recalculation -->",
            "  <set id=\"4\">",
            "    <par type=\"DOUBLE\" name=\"hMin\" value=\"0.000001\"/>",
            "    <par type=\"DOUBLE\" name=\"hMax\" value=\"1\"/>",
            "    <par type=\"DOUBLE\" name=\"kReduceStep\" value=\"0.5\"/>",
            "    <par type=\"INT\" name=\"nEff\" value=\"10\"/>",
            "    <par type=\"INT\" name=\"nDeadband\" value=\"2\"/>",
            "    <par type=\"INT\" name=\"maxRootRestart\" value=\"3\"/>",
            "    <par type=\"INT\" name=\"maxNewtonTry\" value=\"10\"/>",
            "    <par type=\"STRING\" name=\"linearSolverName\" value=\"KLU\"/>",
            "    <par type=\"BOOL\" name=\"recalculateStep\" value=\"true\"/>",
            "  </set>",
            "  <!-- IDA order 2 solver with higher accuracy requirements -->",
            "  <set id=\"5\">",
            "    <par type=\"INT\" name=\"order\" value=\"2\"/>",
            "    <par type=\"DOUBLE\" name=\"initStep\" value=\"0.000001\"/>",
            "    <par type=\"DOUBLE\" name=\"minStep\" value=\"0.000001\"/>",
            "    <par type=\"DOUBLE\" name=\"maxStep\" value=\"10\"/>",
            "    <par type=\"DOUBLE\" name=\"absAccuracy\" value=\"1e-6\"/>",
            "    <par type=\"DOUBLE\" name=\"relAccuracy\" value=\"1e-6\"/>",
            "  </set>",
            "</parametersSet>"));
        return builder.toString();
    }

    private final Network network;
    private final DynawoConfig config;
}
