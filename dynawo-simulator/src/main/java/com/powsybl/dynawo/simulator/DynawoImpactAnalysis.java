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
import com.powsybl.commons.datasource.FileDataSource;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.computation.GroupCommandBuilder;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.dynawo.simulator.input.DynawoCurves;
import com.powsybl.dynawo.simulator.input.DynawoDynamicsModels;
import com.powsybl.dynawo.simulator.input.DynawoJobs;
import com.powsybl.dynawo.simulator.input.DynawoSimulationParameters;
import com.powsybl.dynawo.simulator.input.DynawoSolverParameters;
import com.powsybl.dynawo.simulator.results.DynawoResults;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.simulation.ImpactAnalysis;
import com.powsybl.simulation.ImpactAnalysisProgressListener;
import com.powsybl.simulation.ImpactAnalysisResult;
import com.powsybl.simulation.SimulationParameters;
import com.powsybl.simulation.SimulationState;

public class DynawoImpactAnalysis implements ImpactAnalysis {

    private static final String WORKING_DIR_PREFIX = "powsybl_dynawo_impact_analysis_";
    private static final String OUTPUT_FILE = "outputs/curves/curves.csv";
    private static final String DEFAULT_DYNAWO_CASE_NAME = "nrt/data/IEEE14/IEEE14_BasicTestCases/IEEE14_DisconnectLine/IEEE14.jobs";

    public DynawoImpactAnalysis(Network network, ComputationManager computationManager, int priority,
        ContingenciesProvider contingenciesProvider) {
        this(network, computationManager, priority, contingenciesProvider, DynawoConfig.load());
    }

    public DynawoImpactAnalysis(Network network, ComputationManager computationManager, int priority,
        ContingenciesProvider contingenciesProvider, DynawoConfig config) {
        this.network = network;
        this.computationManager = computationManager;
        this.priority = priority;
        this.config = config;
    }

    private Command createCommand(String dynawoJobsFile) {
        return new GroupCommandBuilder()
            .id("dyn_fs")
            .subCommand()
            .program(config.getDynawoCptCommandName())
            .args("jobs", dynawoJobsFile)
            .add()
            .build();
    }

    protected Command before(SimulationState state, Set<String> contingencyIds, Path workingDir) {
        new DynawoJobs(network).prepareFile(workingDir);
        new DynawoDynamicsModels(network).prepareFile(workingDir);
        new DynawoSimulationParameters(network).prepareFile(workingDir);
        new DynawoSolverParameters(network).prepareFile(workingDir);
        new DynawoCurves(network).prepareFile(workingDir);
        String dynawoJobsFile = DEFAULT_DYNAWO_CASE_NAME;
        if (network != null) {
            Path jobsFile = workingDir.resolve("dynawoModel.jobs");
            Path path = workingDir.resolve(".");
            LOG.info("path {}", path.toString());
            XMLExporter xmlExporter = new XMLExporter();
            xmlExporter.export(network, null, new FileDataSource(path, "dynawoModel"));
            dynawoJobsFile = jobsFile.toAbsolutePath().toString();
        }
        LOG.info("cmd {} jobs {}", config.getDynawoCptCommandName(), dynawoJobsFile);
        return createCommand(dynawoJobsFile);
    }

    protected ImpactAnalysisResult after(Path workingDir, ExecutionReport report) {
        report.log();

        Map<String, String> metrics = new HashMap<>();
        metrics.put("success", report.getErrors().isEmpty() ? "true" : "false");
        DynawoResults results = new DynawoResults(metrics);
        Path file = workingDir.resolve(OUTPUT_FILE);
        if (file.toFile().exists()) {
            results.parseCsv(file);
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
            new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, config.isDebug()),
            new AbstractExecutionHandler<ImpactAnalysisResult>() {

                @Override
                public List<CommandExecution> before(Path workingDir) {
                    Command cmd = DynawoImpactAnalysis.this.before(state, contingencyIds, workingDir);
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
    private final ComputationManager computationManager;
    private final int priority;
    private final DynawoConfig config;
    private static final Logger LOG = LoggerFactory.getLogger(DynawoImpactAnalysis.class);
}
