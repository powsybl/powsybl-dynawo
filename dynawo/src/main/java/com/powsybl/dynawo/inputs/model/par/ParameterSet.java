/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.par;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class ParameterSet {

    private final String id;
    private final Map<String, Parameter> parameters = new HashMap<>();
    private final List<ParameterTable> parameterTables = new ArrayList<>();

    public ParameterSet(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String getId() {
        return id;
    }

    public Map<String, Parameter> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public List<ParameterTable> getParameterTables() {
        return Collections.unmodifiableList(parameterTables);
    }

    public ParameterSet addParameters(List<Parameter> parameters) {
        parameters.forEach(this::addParameter);
        return this;
    }

    public ParameterSet addParameter(Parameter parameter) {
        Objects.requireNonNull(parameter);
        if (parameters.put(parameter.getName(), parameter) != null) {
            LOGGER.warn("parameter {} already exists, the last value entered is retained.", parameter.getName());
        }
        return this;
    }

    public ParameterSet addParameterTables(List<ParameterTable> parameterTable) {
        parameterTables.addAll(parameterTable);
        return this;
    }

    public ParameterSet add(ParameterTable parameterTable) {
        Objects.requireNonNull(parameterTable);
        parameterTables.add(parameterTable);
        return this;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ParameterSet.class);
}
