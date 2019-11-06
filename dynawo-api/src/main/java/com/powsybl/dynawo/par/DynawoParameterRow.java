/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.par;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParameterRow {

    private final int row;
    private final int column;
    private final String value;

    public DynawoParameterRow(int row, int column, String value) {
        this.row = row;
        this.column = column;
        this.value = value;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }

}
