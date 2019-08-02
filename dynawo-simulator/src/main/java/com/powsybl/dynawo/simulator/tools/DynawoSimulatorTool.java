/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.tools;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.google.auto.service.AutoService;
import com.powsybl.commons.config.ComponentDefaultConfig;
import com.powsybl.computation.ComputationManager;
import com.powsybl.dynawo.simulator.DynawoSimulatorFactory;
import com.powsybl.simulation.ImpactAnalysis;
import com.powsybl.simulation.ImpactAnalysisResult;
import com.powsybl.simulation.SimulationParameters;
import com.powsybl.simulation.Stabilization;
import com.powsybl.simulation.StabilizationResult;
import com.powsybl.simulation.StabilizationStatus;
import com.powsybl.tools.Command;
import com.powsybl.tools.Tool;
import com.powsybl.tools.ToolRunningContext;

@AutoService(Tool.class)
public class DynawoSimulatorTool implements Tool {

    @Override
    public Command getCommand() {
        return new Command() {

            @Override
            public String getName() {
                return "dynawo-simulator";
            }

            @Override
            public String getTheme() {
                return "Computation";
            }

            @Override
            public String getDescription() {
                return "Dynawo simulator";
            }

            @Override
            public Options getOptions() {
                return new Options();
            }

            @Override
            public String getUsageFooter() {
                return null;
            }

        };
    }

    @Override
    public void run(CommandLine line, ToolRunningContext context) throws Exception {
        ComponentDefaultConfig defaultConfig = ComponentDefaultConfig.load();
        DynawoSimulatorFactory simulatorFactory = defaultConfig.newFactoryImpl(DynawoSimulatorFactory.class);
        ComputationManager computationManager = context.getShortTimeExecutionComputationManager();
        Stabilization stabilization = simulatorFactory.createStabilization(null, computationManager, 0);
        ImpactAnalysis impactAnalysis = simulatorFactory.createImpactAnalysis(null, computationManager, 0, null);
        Map<String, Object> initContext = new HashMap<>();
        SimulationParameters simulationParameters = SimulationParameters.load();
        stabilization.init(simulationParameters, initContext);
        impactAnalysis.init(simulationParameters, initContext);
        StabilizationResult sr = stabilization.run();
        if (sr.getStatus() == StabilizationStatus.COMPLETED) {
            ImpactAnalysisResult iar = impactAnalysis.run(sr.getState());
        }
    }
}

