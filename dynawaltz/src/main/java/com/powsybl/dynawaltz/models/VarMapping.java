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
public class VarMapping {
    private final String dynamicVar;
    private final String staticVar;

    public VarMapping(String dynamicVar, String staticVar) {
        this.dynamicVar = dynamicVar;
        this.staticVar = staticVar;
    }

    public String getStaticVar() {
        return staticVar;
    }

    public String getDynamicVar() {
        return dynamicVar;
    }
}
