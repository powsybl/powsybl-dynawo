/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.par;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParameterSet {

    private final String id;
    private Map<String, DynawoParameter> parameters;
    private List<DynawoParameterTable> parameterTables;

    public DynawoParameterSet(String id) {
        this.id = id;
        this.parameters = new HashMap<>();
        this.parameterTables = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Map<String, DynawoParameter> getParameters() {
        return parameters;
    }

    public List<DynawoParameterTable> getParameterTables() {
        return parameterTables;
    }

    public DynawoParameterSet addParameters(List<DynawoParameter> parameters) {
        parameters.forEach(p -> add(p));
        return this;
    }

    public DynawoParameterSet add(DynawoParameter parameter) {
        parameters.put(parameter.getName(), parameter);
        LOGGER.warn("parameter {} already exists, the last value entered is retained.", parameter.getName());
        return this;
    }

    public DynawoParameterSet addParameterTables(List<DynawoParameterTable> parameterTable) {
        parameterTables.addAll(parameterTable);
        return this;
    }

    public DynawoParameterSet add(DynawoParameterTable parameterTable) {
        parameterTables.add(parameterTable);
        return this;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoParameterSet.class);
}
