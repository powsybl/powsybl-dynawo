/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.par;

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
public class DynawoParameterSet {

    private final String id;
    private final Map<String, DynawoParameter> parameters = new HashMap<>();
    private final List<DynawoParameterTable> parameterTables = new ArrayList<>();

    public DynawoParameterSet(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String getId() {
        return id;
    }

    public Map<String, DynawoParameter> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public List<DynawoParameterTable> getParameterTables() {
        return Collections.unmodifiableList(parameterTables);
    }

    public DynawoParameterSet addParameters(List<DynawoParameter> parameters) {
        parameters.forEach(this::add);
        return this;
    }

    public DynawoParameterSet add(DynawoParameter parameter) {
        Objects.requireNonNull(parameter);
        if (parameters.containsKey(parameter.getName())) {
            LOGGER.warn("parameter {} already exists, the last value entered is retained.", parameter.getName());
        }
        parameters.put(parameter.getName(), parameter);
        return this;
    }

    public DynawoParameterSet addParameterTables(List<DynawoParameterTable> parameterTable) {
        parameterTables.addAll(parameterTable);
        return this;
    }

    public DynawoParameterSet add(DynawoParameterTable parameterTable) {
        Objects.requireNonNull(parameterTable);
        parameterTables.add(parameterTable);
        return this;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoParameterSet.class);
}
