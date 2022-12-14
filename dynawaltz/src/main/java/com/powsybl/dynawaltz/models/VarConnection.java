/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class VarConnection {
    private final String var1;
    private final String var2;

    public VarConnection(String var1, String var2) {
        this.var1 = var1;
        this.var2 = var2;
    }

    public String getVar1() {
        return var1;
    }

    public String getVar2() {
        return var2;
    }
}
