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

public class DynawoSimulationParameters {

    public DynawoSimulationParameters(DynawoProvider provider) {
        this.parameterSets = provider.getDynawoParameterSets();
    }

    public void prepareFile(Path workingDir) {
        Path parFile = workingDir.resolve("dynawoModel.par");
        try (Writer writer = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), parameterSets()));
        } catch (IOException e) {
            LOGGER.error("Error in file dynawoModel.par");
        }
    }

    private CharSequence parameterSets() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(System.lineSeparator(),
            DynawoInput.setInputHeader(),
            "<parametersSet xmlns=\"http://www.rte-france.com/dynawo\">") + System.lineSeparator());
        int id = 1;
        parameterSets.forEach(parameterSet -> builder.append(String.join(System.lineSeparator(), parameterSet(parameterSet)) + System.lineSeparator()));
        builder.append(String.join(System.lineSeparator(),
            "</parametersSet>") + System.lineSeparator());
        return builder.toString();
    }

    private CharSequence parameterSet(DynawoParameterSet parameterSet) {
        StringBuilder builder = new StringBuilder();
        int id = parameterSet.getId();
        builder.append(String.join(System.lineSeparator(),
            "  <set id=\"" + id + "\">") + System.lineSeparator());
        parameterSet.getParameters().forEach(parameter -> builder.append(String.join(System.lineSeparator(), parameter(parameter)) + System.lineSeparator()));
        builder.append(String.join(System.lineSeparator(),
            "  </set>") + System.lineSeparator());
        return builder.toString();
    }

    private CharSequence parameter(DynawoParameter parameter) {
        if (parameter.isReference()) {
            String name = parameter.getName();
            String type = parameter.getType();
            String origData = parameter.getOrigData();
            String origName = parameter.getOrigName();
            return setReference(name, origData, origName, type);
        } else {
            String name = parameter.getName();
            String type = parameter.getType();
            String value = parameter.getValue();
            return setParameter(type, name, value);
        }
    }

    private String setParameter(String type, String name, String value) {
        return "    <par type=\"" + type + "\" name=\"" + name + "\" value=\"" + value + "\"/>";
    }

    private String setReference(String name, String origData, String origName, String type) {
        return "    <reference name=\"" + name + "\" origData=\"" + origData + "\" origName=\"" + origName
            + "\" type=\"" + type + "\"/>";
    }

    private final List<DynawoParameterSet> parameterSets;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoSimulationParameters.class);
}
