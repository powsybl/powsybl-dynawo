/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.powsybl.dynawo.xml.ParXml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParametersDataBase {

    private static Path defaultParametersFile;

    public static void setDefaultParametersFile(Path parametersFile) {
        defaultParametersFile = parametersFile;
    }

    public static DynawoParametersDataBase load() {
        return ParXml.read(defaultParametersFile);
    }

    public static DynawoParametersDataBase load(Path parametersFile) {
        return ParXml.read(parametersFile);
    }

    public static enum ParameterType {
        DOUBLE, INT, BOOL, STRING;
    }

    public static class ParameterBase {
        public ParameterBase(String name, ParameterType type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public ParameterType getType() {
            return type;
        }

        private final String name;
        private final ParameterType type;
    }

    public static class Parameter extends ParameterBase {
        public Parameter(String name, ParameterType type, String value) {
            super(name, type);
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        private final String value;
    }

    public class Reference extends ParameterBase {
        public Reference(String name, ParameterType type, String origData, String origName) {
            super(name, type);
            this.origData = origData;
            this.origName = origName;
        }

        public String getOrigData() {
            return origData;
        }

        public String getOrigName() {
            return origName;
        }

        private final String origData;
        private final String origName;
        // TODO There is also an optional componentId, not yet supported
    }

    public class ParameterSet {

        public ParameterSet() {
            this.parameters = new HashMap<>();
        }

        public void addParameter(String name, ParameterType type, String value) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(type);
            Objects.requireNonNull(value);

            parameters.put(name, new Parameter(name, type, value));
        }

        public void addReference(String name, ParameterType type, String origData, String origName) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(type);
            Objects.requireNonNull(origData);
            Objects.requireNonNull(origName);

            parameters.put(name, new Reference(name, type, origData, origName));
        }

        public Parameter getParameter(String name) {
            ParameterBase p = parameters.get(name);
            if (p != null && p instanceof Parameter) {
                return (Parameter) p;
            }
            return null;
        }

        public Reference getReference(String name) {
            ParameterBase r = parameters.get(name);
            if (r != null && r instanceof Reference) {
                return (Reference) r;
            }
            return null;
        }

        // TODO parTable
        // In addition to "par" (single values) and "reference" parameters
        // there could be "parTable" elements inside a parameter set
        // that are not yet supported

        private final Map<String, ParameterBase> parameters;

    }

    public DynawoParametersDataBase() {
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
