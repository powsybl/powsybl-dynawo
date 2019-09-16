/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSolver {

    private final String lib;
    private final String file;
    private final int id;

    public DynawoSolver(String lib, String file, int id) {
        this.lib = lib;
        this.file = file;
        this.id = id;
    }

    public String getLib() {
        return lib;
    }

    public String getFile() {
        return file;
    }

    public int getId() {
        return id;
    }
}
