/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.crv;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoCurve {

    private final String model;
    private final String variable;

    public DynawoCurve(String model, String variable) {
        this.model = model;
        this.variable = variable;
    }

    public String getModel() {
        return model;
    }

    public String getVariable() {
        return variable;
    }

}
