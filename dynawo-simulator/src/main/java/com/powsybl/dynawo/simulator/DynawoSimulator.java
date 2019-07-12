package com.powsybl.dynawo.simulator;

import java.util.HashMap;
import java.util.Map;

import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.iidm.network.Network;
import com.powsybl.simulation.ImpactAnalysis;
import com.powsybl.simulation.ImpactAnalysisResult;
import com.powsybl.simulation.SimulationParameters;
import com.powsybl.simulation.Stabilization;
import com.powsybl.simulation.StabilizationResult;
import com.powsybl.simulation.StabilizationStatus;

public class DynawoSimulator {

    public DynawoSimulator(Network network) {
        this.network = network;
    }

    public void simulate() throws Exception {
        ComputationManager computationManager = new LocalComputationManager();
        Stabilization stabilization = new DynawoStabilization(network, computationManager, 0);
        ImpactAnalysis impactAnalysis = new DynawoImpactAnalysis(network, computationManager, 0, null);
        Map<String, Object> initContext = new HashMap<>();
        SimulationParameters simulationParameters = SimulationParameters.load();
        stabilization.init(simulationParameters, initContext);
        impactAnalysis.init(simulationParameters, initContext);
        StabilizationResult sr = stabilization.run();
        if (sr.getStatus() == StabilizationStatus.COMPLETED) {
            ImpactAnalysisResult iar = impactAnalysis.run(sr.getState());
        }
    }

    private final Network network;
}
