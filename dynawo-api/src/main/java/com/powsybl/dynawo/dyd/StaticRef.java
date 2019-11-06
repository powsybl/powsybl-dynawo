/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class StaticRef {

    private final String var;
    private final String staticVar;

    public StaticRef(String var, String staticVar) {
        this.var = var;
        this.staticVar = staticVar;
    }

    public String getVar() {
        return var;
    }

    public String getStaticVar() {
        return staticVar;
    }

}
