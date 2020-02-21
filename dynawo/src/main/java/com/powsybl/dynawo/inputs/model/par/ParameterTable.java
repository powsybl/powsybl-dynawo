/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.par;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class ParameterTable {

    private final String type;
    private final String name;
    private final List<ParameterRow> parameterRows = new ArrayList<>();

    public ParameterTable(String type, String name) {
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<ParameterRow> getParameterRows() {
        return Collections.unmodifiableList(parameterRows);
    }

    public ParameterTable addParameterRows(List<ParameterRow> parameterRow) {
        parameterRows.addAll(parameterRow);
        return this;
    }

    public ParameterTable add(ParameterRow parameterRow) {
        Objects.requireNonNull(parameterRow);
        parameterRows.add(parameterRow);
        return this;
    }
}
