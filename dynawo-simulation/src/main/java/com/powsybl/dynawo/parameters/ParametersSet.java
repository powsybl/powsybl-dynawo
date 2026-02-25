/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.powsybl.commons.PowsyblException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public class ParametersSet {

    private final Map<String, Parameter> parameters;
    private final Map<String, Reference> references;
    private final String id;
    private static final String ORIGIN_DATA = "IIDM";

    public ParametersSet(@JsonProperty("id") String id) {
        this.id = id;
        this.parameters = new LinkedHashMap<>();
        this.references = new LinkedHashMap<>();
    }

    public ParametersSet(String id, ParametersSet parametersSet) {
        this.id = id;
        this.parameters = new LinkedHashMap<>(parametersSet.parameters);
        this.references = new LinkedHashMap<>(parametersSet.references);
    }

    public void addParameter(String name, ParameterType type, String value) {
        parameters.put(name, new Parameter(name, type, value));
    }

    public void addParameter(Parameter parameter) {
        parameters.put(parameter.name(), parameter);
    }

    public void replaceParameter(String parameterName, ParameterType type, String value) {
        parameters.replace(parameterName, new Parameter(parameterName, type, value));
    }

    public void addReference(String name, ParameterType type, String origName, String componentId) {

        if (name == null || name.isEmpty()) {
            return;
        }

        if (parameters.containsKey(name) || references.containsKey(name)) {
            return;
        }

        references.put(name, new Reference(name, type, ORIGIN_DATA, origName, componentId));
    }

    public void addReference(String name, ParameterType type, String origName) {
        addReference(name, type, origName, null);
    }

    public String getId() {
        return id;
    }

    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    public Map<String, Reference> getReferences() {
        return references;
    }

    public boolean getBool(String parameterName) {
        Parameter parameter = getParameter(parameterName, ParameterType.BOOL);
        return Boolean.parseBoolean(parameter.value());
    }

    public double getDouble(String parameterName) {
        Parameter parameter = getParameter(parameterName, ParameterType.DOUBLE);
        return Double.parseDouble(parameter.value());
    }

    public int getInt(String parameterName) {
        Parameter parameter = getParameter(parameterName, ParameterType.INT);
        return Integer.parseInt(parameter.value());
    }

    public String getString(String parameterName) {
        Parameter parameter = getParameter(parameterName, ParameterType.STRING);
        return parameter.value();
    }

    public boolean hasParameter(String parameterName) {
        return parameters.containsKey(parameterName);
    }

    private Parameter getParameter(String parameterName, ParameterType type) {
        Parameter parameter = parameters.get(parameterName);
        if (parameter == null) {
            throw new IllegalArgumentException("Parameter " + parameterName + " not found in set " + id);
        }
        if (parameter.type() != type) {
            throw new PowsyblException("Invalid parameter type: " + parameter.type() + " (" + type + " expected)");
        }
        return parameter;
    }

    @Override
    public String toString() {
        return StringUtils.joinWith(",", id, StringUtils.join(parameters), StringUtils.join(references));
    }
}
