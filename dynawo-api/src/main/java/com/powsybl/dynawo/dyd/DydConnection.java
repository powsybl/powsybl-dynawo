/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DydConnection {

    private final String var1;
    private final String var2;

    public DydConnection(String var1, String var2) {
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
