/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.par;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParameterSet {

    private final int id;
    private List<DynawoParameter> parameters;
    private List<DynawoParameterTable> parameterTables;

    public DynawoParameterSet(int id) {
        this.id = id;
        this.parameters = new ArrayList<>();
        this.parameterTables = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public List<DynawoParameter> getParameters() {
        return parameters;
    }

    public List<DynawoParameterTable> getParameterTables() {
        return parameterTables;
    }

    public DynawoParameterSet addParameters(List<DynawoParameter> parameter) {
        parameters.addAll(parameter);
        return this;
    }

    public DynawoParameterSet add(DynawoParameter parameter) {
        parameters.add(parameter);
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
}
