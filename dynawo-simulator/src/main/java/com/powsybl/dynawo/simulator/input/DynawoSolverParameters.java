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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSolverParameters {

    private static final String DOUBLE = "DOUBLE";

    public DynawoSolverParameters(Network network) {
        this.network = network;
    }

    public void prepareFile(Path workingDir) {
        Path parFile = workingDir.resolve("solvers.par");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), parameters()));
        } catch (IOException e) {
            LOGGER.error("Error in file solvers.par");
        }
    }

    private CharSequence parameters() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            DynawoInput.setInputHeader(),
            "<parametersSet xmlns=\"http://www.rte-france.com/dynawo\">",
            "  <!-- IDA order 1 solver-->",
            openSet(1),
            setParameter(DOUBLE, "order", "1"),
            setParameter(DOUBLE, "initStep", "0.000001"),
            setParameter(DOUBLE, "minStep", "0.000001"),
            setParameter(DOUBLE, "maxStep", "10"),
            setParameter(DOUBLE, "absAccuracy", "1e-4"),
            setParameter(DOUBLE, "relAccuracy", "1e-4"),
            closeSet(),
            "  <!-- IDA order 2 solver -->",
            openSet(2),
            setParameter(DOUBLE, "order", "2"),
            setParameter(DOUBLE, "initStep", "0.000001"),
            setParameter(DOUBLE, "minStep", "0.000001"),
            setParameter(DOUBLE, "maxStep", "10"),
            setParameter(DOUBLE, "absAccuracy", "1e-4"),
            setParameter(DOUBLE, "relAccuracy", "1e-4"),
            closeSet(),
            "  <!-- Simplified solver without step recalculation -->",
            openSet(3),
            setParameter(DOUBLE, "hMin", "0.000001"),
            setParameter(DOUBLE, "hMax", "1"),
            setParameter(DOUBLE, "kReduceStep", "0.5"),
            setParameter(DOUBLE, "nEff", "10"),
            setParameter(DOUBLE, "nDeadband", "2"),
            setParameter(DOUBLE, "maxRootRestart", "3"),
            setParameter(DOUBLE, "maxNewtonTry", "10"),
            setParameter(DOUBLE, "linearSolverName", "KLU"),
            setParameter(DOUBLE, "recalculateStep", "false"),
            closeSet(),
            "  <!-- Simplified solver with step recalculation -->",
            openSet(4),
            setParameter(DOUBLE, "hMin", "0.000001"),
            setParameter(DOUBLE, "hMax", "1"),
            setParameter(DOUBLE, "kReduceStep", "0.5"),
            setParameter(DOUBLE, "nEff", "10"),
            setParameter(DOUBLE, "nDeadband", "2"),
            setParameter(DOUBLE, "maxRootRestart", "3"),
            setParameter(DOUBLE, "maxNewtonTry", "10"),
            setParameter(DOUBLE, "linearSolverName", "KLU"),
            setParameter(DOUBLE, "recalculateStep", "true"),
            closeSet(),
            "  <!-- IDA order 2 solver with higher accuracy requirements -->",
            openSet(5),
            setParameter(DOUBLE, "order", "2"),
            setParameter(DOUBLE, "initStep", "0.000001"),
            setParameter(DOUBLE, "minStep", "0.000001"),
            setParameter(DOUBLE, "maxStep", "10"),
            setParameter(DOUBLE, "absAccuracy", "1e-6"),
            setParameter(DOUBLE, "relAccuracy", "1e-6"),
            closeSet(),
            "</parametersSet>") + System.lineSeparator());
        return builder.toString();
    }

    private String openSet(int id) {
        return "  <set id=\"" + id + "\">";
    }

    private String closeSet() {
        return "  </set>";
    }

    private String setParameter(String type, String name, String value) {
        return "    <par type=\"" + type + "\" name=\"" + name + "\" value=\"" + value + "\"/>";
    }

    private final Network network;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoSolverParameters.class);
}
