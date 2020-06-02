/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawo.simulator;

import com.powsybl.dynamicsimulation.Curve;

import java.util.Objects;

/**
 * @author Mathieu Bague <mathieu.bague@rte-france.com>
 */
public class DynawoCurve implements Curve {

    private final String modelId;
    private final String variable;

    public DynawoCurve(String modelId, String variable) {
        this.modelId = Objects.requireNonNull(modelId);
        this.variable = Objects.requireNonNull(variable);
    }

    public String getModelId() {
        return modelId;
    }

    public String getVariable() {
        return variable;
    }
}
