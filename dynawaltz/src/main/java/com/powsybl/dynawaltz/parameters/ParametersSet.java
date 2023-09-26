/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.powsybl.commons.PowsyblException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class ParametersSet {

    private final Map<String, Parameter> parameters = new LinkedHashMap<>();

    private final List<Reference> references = new ArrayList<>();
    private final String id;

    public ParametersSet(@JsonProperty("id") String id) {
        this.id = id;
    }

    public void addParameter(String name, ParameterType type, String value) {
        parameters.put(name, new Parameter(name, type, value));
    }

    public void addReference(String name, ParameterType type, String origData, String origName, String componentId) {
        references.add(new Reference(name, type, origData, origName, componentId));
    }

    public String getId() {
        return id;
    }

    public Map<String, Parameter> getParameters() {
        return parameters;
    }

    public List<Reference> getReferences() {
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

    public Parameter getParameter(String parameterName) {
        Parameter parameter = parameters.get(parameterName);
        if (parameter == null) {
            throw new IllegalArgumentException("Parameter " + parameterName + " not found in set " + id);
        }
        return parameter;
    }

    private Parameter getParameter(String parameterName, ParameterType type) {
        Parameter parameter = getParameter(parameterName);
        if (parameter.type() != type) {
            throw new PowsyblException("Invalid parameter type: " + parameter.type() + " (" + type + " expected)");
        }
        return parameter;
    }
}
