package com.powsybl.dynawo.simulator;

import java.util.Map;

import com.powsybl.simulation.SimulationState;
import com.powsybl.simulation.StabilizationResult;
import com.powsybl.simulation.StabilizationStatus;

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
