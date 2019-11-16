/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.par;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParameterSet {

    private final int id;
    private final List<DynawoParameter> parameters = new ArrayList<>();
    private final List<DynawoParameterTable> parameterTables = new ArrayList<>();

    public DynawoParameterSet(int id) {
        this.id = Objects.requireNonNull(id);
    }

    public int getId() {
        return id;
    }

    public List<DynawoParameter> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public List<DynawoParameterTable> getParameterTables() {
        return Collections.unmodifiableList(parameterTables);
    }

    public DynawoParameterSet addParameters(List<DynawoParameter> parameter) {
        parameters.addAll(parameter);
        return this;
    }

    public DynawoParameterSet add(DynawoParameter parameter) {
        Objects.requireNonNull(parameter);
        parameters.add(parameter);
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
}
