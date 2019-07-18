/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
        this(network, DynawoConfig.load());
    }

    public DynawoSimulator(Network network, DynawoConfig config) {
        this.network = network;
        this.config = config;
    }

    public void simulate() throws Exception {
        ComputationManager computationManager = new LocalComputationManager();
        Stabilization stabilization = new DynawoStabilization(network, computationManager, 0);
        ImpactAnalysis impactAnalysis = new DynawoImpactAnalysis(network, computationManager, 0, null, config);
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
    private final DynawoConfig config;
}
