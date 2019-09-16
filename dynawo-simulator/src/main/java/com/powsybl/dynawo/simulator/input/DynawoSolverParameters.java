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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.dynawo.DynawoParameter;
import com.powsybl.dynawo.DynawoParameterSet;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSolverParameters {

    public DynawoSolverParameters(Network network, DynawoProvider provider) {
        this.parameterSets = provider.getDynawoSolverParameterSets(network);
    }

    public void prepareFile(Path workingDir) {
        Path parFile = workingDir.resolve("solvers.par");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), writeParameterSets()));
        } catch (IOException e) {
            LOGGER.error("Error in file solvers.par");
        }
    }

    private String writeParameterSets() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            DynawoInput.writeInputHeader(),
            "<parametersSet xmlns=\"http://www.rte-france.com/dynawo\">") + System.lineSeparator());
        parameterSets.forEach(parameterSet -> builder
            .append(String.join(System.lineSeparator(), writeParameterSet(parameterSet)) + System.lineSeparator()));
        builder.append(String.join(System.lineSeparator(),
            "</parametersSet>") + System.lineSeparator());
        return builder.toString();
    }

    private String writeParameterSet(DynawoParameterSet parameterSet) {
        StringBuilder builder = new StringBuilder();
        int id = parameterSet.getId();
        builder.append(String.join(System.lineSeparator(),
            "  <set id=\"" + id + "\">") + System.lineSeparator());
        parameterSet.getParameters().forEach(parameter -> builder
            .append(String.join(System.lineSeparator(), writeParameter(parameter)) + System.lineSeparator()));
        builder.append(String.join(System.lineSeparator(),
            "  </set>") + System.lineSeparator());
        return builder.toString();
    }

    private String writeParameter(DynawoParameter parameter) {
        String name = parameter.getName();
        String type = parameter.getType();
        String value = parameter.getValue();
        return writeParameter(type, name, value);
    }

    private String writeParameter(String type, String name, String value) {
        return "    <par type=\"" + type + "\" name=\"" + name + "\" value=\"" + value + "\"/>";
    }

    private final List<DynawoParameterSet> parameterSets;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoSolverParameters.class);
}
