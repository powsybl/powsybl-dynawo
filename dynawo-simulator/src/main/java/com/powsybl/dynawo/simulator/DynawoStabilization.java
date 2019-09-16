/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.powsybl.computation.ComputationManager;
import com.powsybl.iidm.network.Network;
import com.powsybl.simulation.SimulationParameters;
import com.powsybl.simulation.Stabilization;
import com.powsybl.simulation.StabilizationResult;
import com.powsybl.simulation.StabilizationStatus;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoStabilization implements Stabilization {

    public DynawoStabilization(Network network, ComputationManager computationManager, int priority) {
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public void init(SimulationParameters parameters, Map<String, Object> context) {
        // empty default implementation
    }

    @Override
    public StabilizationResult run() {
        return new DynawoStabilizationResult(StabilizationStatus.COMPLETED, null, new DynawoState());
    }

    @Override
    public CompletableFuture<StabilizationResult> runAsync(String workingStateId) {
        return null;
    }

}
