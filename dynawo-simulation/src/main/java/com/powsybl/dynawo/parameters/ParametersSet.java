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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public class ParametersSet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParametersSet.class);
    private final Map<String, Parameter> parameters;
    private final List<Reference> references;
    private final Map<String, Map<String, PrefixParameter>> prefixParameters;
    private final String id;

    public ParametersSet(@JsonProperty("id") String id) {
        this.id = id;
        this.parameters = new LinkedHashMap<>();
        this.references = new ArrayList<>();
        this.prefixParameters = new HashMap<>();
    }

    public ParametersSet(String id, ParametersSet parametersSet) {
        this.id = id;
        this.parameters = new LinkedHashMap<>(parametersSet.parameters);
        this.references = new ArrayList<>(parametersSet.references);
        this.prefixParameters = new HashMap<>(parametersSet.prefixParameters);
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

    public void addReference(String name, ParameterType type, String origData, String origName, String componentId) {
        references.add(new Reference(name, type, origData, origName, componentId));
    }

    public void addReference(String name, ParameterType type, String origData, String origName) {
        references.add(new Reference(name, type, origData, origName, null));
    }

    public void addPrefixParameter(String name, String componentId, ParameterType type, String value) {
        prefixParameters.computeIfAbsent(name, k -> new HashMap<>())
                .put(componentId, new PrefixParameter(name, componentId, type, value));
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

    /**
     * Create one parameter by componentId from prefix parameters in the form of name_N
     * @param name name of the parameter that will be used as prefix
     * @param componentIds componentIds of the PrefixParameter
     */
    public void generateParametersFromPrefix(String name, List<String> componentIds) {
        Map<String, PrefixParameter> prefixParametersMap = prefixParameters.get(name);
        if (prefixParametersMap != null) {
            for (int i = 0; i < componentIds.size(); i++) {
                PrefixParameter prefixParameter = prefixParametersMap.get(componentIds.get(i));
                if (prefixParameter != null) {
                    addParameter(name + "_" + i, prefixParameter.type(), prefixParameter.value());
                } else {
                    LOGGER.warn("Prefix parameter {} for equipment {} not found, the associated parameter cannot be created", name, componentIds.get(i));
                }
            }
        } else {
            LOGGER.warn("Prefix parameters {} not found, all the associated parameters for equipments {} cannot be created", name, componentIds.toString());
        }
    }

    @Override
    public String toString() {
        return StringUtils.joinWith(",", id, StringUtils.join(parameters), StringUtils.join(references),
                StringUtils.join(prefixParameters));
    }
}
