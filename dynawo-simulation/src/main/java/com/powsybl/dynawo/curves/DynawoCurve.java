/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.curves;

import com.powsybl.dynamicsimulation.Curve;

import java.util.Objects;

/**
 * @author Mathieu Bague {@literal <mathieu.bague@rte-france.com>}
 */
public class DynawoCurve implements Curve {

    private final String dynamicModelId;
    private final String variable;

    DynawoCurve(String dynamicModelId, String variable) {
        this.dynamicModelId = Objects.requireNonNull(dynamicModelId);
        this.variable = Objects.requireNonNull(variable);
    }

    public String getModelId() {
        return dynamicModelId;
    }

    public String getVariable() {
        return variable;
    }
}
