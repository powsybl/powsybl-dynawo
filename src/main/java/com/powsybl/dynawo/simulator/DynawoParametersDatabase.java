/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.powsybl.dynawo.xml.PararametersXml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParametersDatabase {

    private static Path defaultParametersFile;

    public static void setDefaultParametersFile(Path parametersFile) {
        defaultParametersFile = parametersFile;
    }

    public static DynawoParametersDatabase load() throws IOException {
        return PararametersXml.read(defaultParametersFile);
    }

    public static DynawoParametersDatabase load(Path parametersFile) throws IOException {
        return PararametersXml.read(parametersFile);
    }

    public static enum ParameterType {
        DOUBLE, INT, BOOL, STRING;
    }

    public static class Parameter {
        public Parameter(String name, ParameterType type, String value) {
            this.name = Objects.requireNonNull(name);
            this.type = Objects.requireNonNull(type);
            this.value = Objects.requireNonNull(value);
        }

        public String getName() {
            return name;
        }

        public ParameterType getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        private final String name;
        private final ParameterType type;
        private final String value;
    }

    public class ParameterSet {

        public ParameterSet() {
            this.parameters = new HashMap<>();
        }

        public void addParameter(String name, ParameterType type, String value) {
            parameters.put(name, new Parameter(name, type, value));
        }

        public Parameter getParameter(String name) {
            return parameters.get(name);
        }

        private final Map<String, Parameter> parameters;

    }

    public DynawoParametersDatabase() {
        this.parameterSets = new HashMap<>();
    }

    public void addParameterSet(String parameterSetId, ParameterSet parameterSet) {
        parameterSets.put(parameterSetId, parameterSet);
    }

    public ParameterSet getParameterSet(String parameterSetId) {
        return parameterSets.get(parameterSetId);
    }

    private Map<String, ParameterSet> parameterSets;

}
