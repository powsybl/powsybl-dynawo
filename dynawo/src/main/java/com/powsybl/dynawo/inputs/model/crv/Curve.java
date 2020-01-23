/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.model.crv;

import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Curve {

    private final String model;
    private final String variable;

    public Curve(String model, String variable) {
        this.model = Objects.requireNonNull(model);
        this.variable = Objects.requireNonNull(variable);
    }

    public String getModel() {
        return model;
    }

    public String getVariable() {
        return variable;
    }

}
