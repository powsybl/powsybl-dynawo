/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawo.simulator;

import com.powsybl.dynamicsimulation.Curve;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Mathieu Bague <mathieu.bague@rte-france.com>
 */
public class CurveImpl implements Curve {

    private final String modelId;

    private final List<String> variables;

    public CurveImpl(String modelId, List<String> variables) {
        this.modelId = Objects.requireNonNull(modelId);
        this.variables = Objects.requireNonNull(variables);
    }

    public String getModelId() {
        return modelId;
    }

    public List<String> getVariables() {
        return Collections.unmodifiableList(variables);
    }
}
