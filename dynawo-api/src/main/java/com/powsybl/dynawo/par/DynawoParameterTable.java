/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
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
public class DynawoParameterTable {

    private final String type;
    private final String name;
    private List<DynawoParameterRow> parameterRows;

    public DynawoParameterTable(String type, String name) {
        this.type = type;
        this.name = name;
        this.parameterRows = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<DynawoParameterRow> getParameterRows() {
        return parameterRows;
    }

    public DynawoParameterTable addParameterRows(List<DynawoParameterRow> parameterRow) {
        parameterRows.addAll(parameterRow);
        return this;
    }

    public DynawoParameterTable add(DynawoParameterRow parameterRow) {
        parameterRows.add(parameterRow);
        return this;
    }
}
