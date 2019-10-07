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
import com.powsybl.dynawo.DynawoExporter;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.simulation.ImpactAnalysis;
import com.powsybl.simulation.ImpactAnalysisResult;
import com.powsybl.simulation.SimulationParameters;
import com.powsybl.simulation.Stabilization;
import com.powsybl.simulation.StabilizationResult;
import com.powsybl.simulation.StabilizationStatus;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulator {

    public DynawoSimulator(DynawoExporter exporter) {
        this(SimulationParameters.load(), LocalComputationManager.getDefault(), exporter, DynawoConfig.load());
    }

    public DynawoSimulator(SimulationParameters simulationParameters, ComputationManager computationManager,
        DynawoExporter exporter, DynawoConfig dynawoConfig) {
        this.simulationParameters = simulationParameters;
        this.computationManager = computationManager;
        this.dynawoConfig = dynawoConfig;
        this.exporter = exporter;
    }

    public ImpactAnalysisResult simulate(Network network, DynawoProvider dynawoProvider) throws Exception {
        Stabilization stabilization = new DynawoStabilization(network, computationManager, 0);
        ImpactAnalysis impactAnalysis = new DynawoImpactAnalysis(network, computationManager, 0, dynawoProvider,
            exporter, dynawoConfig);
        Map<String, Object> initContext = new HashMap<>();
        stabilization.init(simulationParameters, initContext);
        impactAnalysis.init(simulationParameters, initContext);
        StabilizationResult sr = stabilization.run();
        if (sr.getStatus() == StabilizationStatus.COMPLETED) {
            return impactAnalysis.run(sr.getState());
        }
        return null;
    }

    private final ComputationManager computationManager;
    private final SimulationParameters simulationParameters;
    private final DynawoConfig dynawoConfig;
    private final DynawoExporter exporter;
}
