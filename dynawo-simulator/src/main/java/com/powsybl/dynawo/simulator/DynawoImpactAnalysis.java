/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.computation.GroupCommandBuilder;
import com.powsybl.dynawo.DynawoExporter;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.dynawo.simulator.results.DynawoResults;
import com.powsybl.iidm.network.Network;
import com.powsybl.simulation.ImpactAnalysis;
import com.powsybl.simulation.ImpactAnalysisProgressListener;
import com.powsybl.simulation.ImpactAnalysisResult;
import com.powsybl.simulation.SimulationParameters;
import com.powsybl.simulation.SimulationState;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoImpactAnalysis implements ImpactAnalysis {

    private static final String WORKING_DIR_PREFIX = "powsybl_dynawo_impact_analysis_";
    private static final String OUTPUT_FILE = "curves/curves.csv";

    public DynawoImpactAnalysis(Network network, ComputationManager computationManager, int priority,
        DynawoProvider dynawoProvider, DynawoExporter dynawoExporter) {
        this(network, computationManager, priority, dynawoProvider, dynawoExporter, DynawoConfig.load());
    }

    public DynawoImpactAnalysis(Network network, ComputationManager computationManager, int priority,
        DynawoProvider dynawoProvider, DynawoExporter dynawoExporter, DynawoConfig dynawoConfig) {
        this.network = network;
        this.computationManager = computationManager;
        this.priority = priority;
        this.dynawoProvider = dynawoProvider;
        this.dynawoExporter = dynawoExporter;
        this.dynawoConfig = dynawoConfig;
    }

    private Command createCommand(String dynawoJobsFile) {
        return new GroupCommandBuilder()
            .id("dyn_fs")
            .subCommand()
            .program(dynawoConfig.getDynawoCptCommandName())
            .args("jobs", dynawoJobsFile)
            .add()
            .build();
    }

    private Command before(Path workingDir) {
        String dynawoJobsFile = dynawoExporter.export(network, dynawoProvider, workingDir);
        LOGGER.info("cmd {} jobs {}", dynawoConfig.getDynawoCptCommandName(), dynawoJobsFile);
        return createCommand(dynawoJobsFile);
    }

    private ImpactAnalysisResult after(Path workingDir, ExecutionReport report) {
        report.log();

        Map<String, String> metrics = new HashMap<>();
        metrics.put("success", Boolean.toString(report.getErrors().isEmpty()));
        DynawoResults results = new DynawoResults(metrics);
        Path file = workingDir.resolve(dynawoProvider.getDynawoJobs(network).get(0).getOutputs().getDirectory())
            .resolve(OUTPUT_FILE);
        try {
            if (file.toFile().exists()) {
                results.parseCsv(file);
            }
        } catch (Exception x) {
            LOGGER.error(x.toString());
            metrics.put("success", "false");
        }
        return results;
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
    public ImpactAnalysisResult run(SimulationState state) {
        return runAsync(state, null, null).join();
    }

    @Override
    public ImpactAnalysisResult run(SimulationState state, Set<String> contingencyIds) {
        return runAsync(state, contingencyIds, null).join();
    }

    @Override
    public CompletableFuture<ImpactAnalysisResult> runAsync(SimulationState state, Set<String> contingencyIds,
        ImpactAnalysisProgressListener listener) {
        return computationManager.execute(
            new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynawoConfig.isDebug()),
            new AbstractExecutionHandler<ImpactAnalysisResult>() {

                @Override
                public List<CommandExecution> before(Path workingDir) {
                    Command cmd = DynawoImpactAnalysis.this.before(workingDir);
                    return Collections.singletonList(
                        new CommandExecution(cmd, 1, priority, ImmutableMap.of("state", state.getName())));
                }

                @Override
                public ImpactAnalysisResult after(Path workingDir, ExecutionReport report) {
                    return DynawoImpactAnalysis.this.after(workingDir, report);
                }
            });
    }

    private final Network network;
    private final DynawoProvider dynawoProvider;
    private final DynawoExporter dynawoExporter;
    private final ComputationManager computationManager;
    private final int priority;
    private final DynawoConfig dynawoConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoImpactAnalysis.class);
}
