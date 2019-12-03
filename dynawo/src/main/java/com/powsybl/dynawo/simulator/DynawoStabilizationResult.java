/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.util.Map;

import com.powsybl.simulation.SimulationState;
import com.powsybl.simulation.StabilizationResult;
import com.powsybl.simulation.StabilizationStatus;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoStabilizationResult implements StabilizationResult {

    public DynawoStabilizationResult(StabilizationStatus completed, Map<String, String> metrics, DynawoState state) {
        this.status = completed;
        this.metrics = metrics;
        this.state = state;
    }

    @Override
    public StabilizationStatus getStatus() {
        return status;
    }

    @Override
    public Map<String, String> getMetrics() {
        return metrics;
    }

    @Override
    public SimulationState getState() {
        return state;
    }

    private final StabilizationStatus status;
    private final Map<String, String> metrics;
    private final DynawoState state;
}
