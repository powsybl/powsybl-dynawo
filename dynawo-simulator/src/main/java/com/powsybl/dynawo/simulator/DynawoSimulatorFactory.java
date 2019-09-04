package com.powsybl.dynawo.simulator;

import com.powsybl.computation.ComputationManager;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.simulation.ImpactAnalysis;
import com.powsybl.simulation.Stabilization;

public interface DynawoSimulatorFactory {

    public Stabilization createStabilization(Network network, ComputationManager computationManager, int priority);

    public ImpactAnalysis createImpactAnalysis(Network network, ComputationManager computationManager, int priority,
        DynawoProvider dynawoProvider);
}
