package com.powsybl.dynawo.simulator;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.powsybl.computation.ComputationManager;
import com.powsybl.iidm.network.Network;
import com.powsybl.simulation.SimulationParameters;
import com.powsybl.simulation.Stabilization;
import com.powsybl.simulation.StabilizationResult;
import com.powsybl.simulation.StabilizationStatus;

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
    public void init(SimulationParameters parameters, Map<String, Object> context) throws Exception {
    }

    @Override
    public StabilizationResult run() throws Exception {
        return new DynawoStabilizationResult(StabilizationStatus.COMPLETED, null, new DynawoState());
    }

    @Override
    public CompletableFuture<StabilizationResult> runAsync(String workingStateId) {
        return null;
    }

}
