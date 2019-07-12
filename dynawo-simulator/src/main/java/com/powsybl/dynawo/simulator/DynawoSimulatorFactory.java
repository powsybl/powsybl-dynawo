package com.powsybl.dynawo.simulator;

import com.google.auto.service.AutoService;
import com.powsybl.computation.ComputationManager;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.simulation.ImpactAnalysis;
import com.powsybl.simulation.SimulatorFactory;
import com.powsybl.simulation.Stabilization;

@AutoService(SimulatorFactory.class)
public class DynawoSimulatorFactory implements SimulatorFactory {

    @Override
    public Stabilization createStabilization(Network network, ComputationManager computationManager, int priority) {
        return new DynawoStabilization(network, computationManager, priority);
    }

    @Override
    public ImpactAnalysis createImpactAnalysis(Network network, ComputationManager computationManager, int priority,
        ContingenciesProvider contingenciesProvider) {
        return new DynawoImpactAnalysis(network, computationManager, priority, contingenciesProvider);
    }

}
