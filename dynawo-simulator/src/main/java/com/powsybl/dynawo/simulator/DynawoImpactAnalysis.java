package com.powsybl.dynawo.simulator;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
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
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.simulation.ImpactAnalysis;
import com.powsybl.simulation.ImpactAnalysisProgressListener;
import com.powsybl.simulation.ImpactAnalysisResult;
import com.powsybl.simulation.SimulationParameters;
import com.powsybl.simulation.SimulationState;

public class DynawoImpactAnalysis implements ImpactAnalysis {

    private static final String WORKING_DIR_PREFIX = "powsybl_dynawo_impact_analysis_";
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
        cmd = createCommand();
    }

    private Command createCommand() {
        String dynawoJobsFile = DEFAULT_DYNAWO_CASE_NAME;
        if (network != null) {
            Path jobsFile = config.getWorkingDir().resolve("dynawoModel.jobs");
            try (Writer writer = Files.newBufferedWriter(jobsFile, StandardCharsets.UTF_8)) {
                writer.write(String.join(System.lineSeparator(),
                    "<?xml version='1.0' encoding='UTF-8'?>",
                    "<!--",
                    "    Copyright (c) 2015-2019, RTE (http://www.rte-france.com)",
                    "    See AUTHORS.txt",
                    "    All rights reserved.",
                    "    This Source Code Form is subject to the terms of the Mozilla Public",
                    "    License, v. 2.0. If a copy of the MPL was not distributed with this",
                    "    file, you can obtain one at http://mozilla.org/MPL/2.0/.",
                    "    SPDX-License-Identifier: MPL-2.0",
                    "",
                    "    This file is part of Dynawo, an hybrid C++/Modelica open source time domain",
                    "    simulation tool for power systems.",
                    "-->",
                    "<dyn:jobs xmlns:dyn=\"http://www.rte-france.com/dynawo\">",
                    "  <dyn:job name=\"IEEE14 - Disconnect Line\">",
                    "    <dyn:solver lib=\"libdynawo_SolverIDA\" parFile=\"solvers.par\" parId=\"2\"/>",
                    "    <dyn:modeler compileDir=\"outputs/compilation\">",
                    "      <dyn:network iidmFile=\"dynawoModel.iidm\" parFile=\"dynawoModel.par\" parId=\"1\"/>",
                    "      <dyn:dynModels dydFile=\"dynawoModel.dyd\"/>",
                    "      <dyn:precompiledModels useStandardModels=\"true\"/>",
                    "      <dyn:modelicaModels useStandardModels=\"true\"/>",
                    "    </dyn:modeler>",
                    "    <dyn:simulation startTime=\"0\" stopTime=\"30\" activateCriteria=\"false\"/>",
                    "    <dyn:outputs directory=\"outputs\">",
                    "      <dyn:dumpInitValues local=\"true\" global=\"true\"/>",
                    "      <!--dyn:curves inputFile=\"dynawoModel.crv\" exportMode=\"CSV\"/-->",
                    "      <dyn:timeline exportMode=\"TXT\"/>",
                    "      <dyn:logs>",
                    "        <dyn:appender tag=\"\" file=\"dynawo.log\" lvlFilter=\"DEBUG\"/>",
                    "        <dyn:appender tag=\"COMPILE\" file=\"dynawoCompiler.log\" lvlFilter=\"DEBUG\"/>",
                    "        <dyn:appender tag=\"MODELER\" file=\"dynawoModeler.log\" lvlFilter=\"DEBUG\"/>",
                    "      </dyn:logs>",
                    "    </dyn:outputs>",
                    "  </dyn:job>",
                    "</dyn:jobs>"));
                Path path = config.getWorkingDir().resolve(".");
                XMLExporter xmlExporter = new XMLExporter();
                xmlExporter.export(network, null, new FileDataSource(path, "dynawoModel.iidm"));
                dynawoJobsFile = jobsFile.toAbsolutePath().toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        LOG.info("cmd {} jobs {}", config.getDynawoCptCommandName(), dynawoJobsFile);
        return new GroupCommandBuilder()
            .id("dyn_fs")
            .subCommand()
            .program(config.getDynawoCptCommandName())
            .args("jobs", dynawoJobsFile)
            .add()
            .build();
    }

    protected Command before(SimulationState state, Set<String> contingencyIds, Path workingDir) {
        new DynawoDynamicsModels(network, config).prepareFile();
        new DynawoSimulationParameters(network, config).prepareFile();
        new DynawoSolverParameters(network, config).prepareFile();
        return cmd;
    }

    protected ImpactAnalysisResult after(Path workingDir, ExecutionReport report) {
        return null;
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
    public ImpactAnalysisResult run(SimulationState state) throws Exception {
        return runAsync(state, null, null).join();
    }

    @Override
    public ImpactAnalysisResult run(SimulationState state, Set<String> contingencyIds) throws Exception {
        return runAsync(state, contingencyIds, null).join();
    }

    @Override
    public CompletableFuture<ImpactAnalysisResult> runAsync(SimulationState state, Set<String> contingencyIds,
        ImpactAnalysisProgressListener listener) {
        return computationManager.execute(
            new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, config.isDebug()),
            new AbstractExecutionHandler<ImpactAnalysisResult>() {

                @Override
                public List<CommandExecution> before(Path workingDir) throws IOException {
                    Command cmd = DynawoImpactAnalysis.this.before(state, contingencyIds, workingDir);
                    return Collections.singletonList(
                        new CommandExecution(cmd, 1, priority, ImmutableMap.of("state", state.getName())));
                }

                @Override
                public ImpactAnalysisResult after(Path workingDir, ExecutionReport report) throws IOException {
                    return DynawoImpactAnalysis.this.after(workingDir, report);
                }
            });
    }

    private final Network network;
    private final Command cmd;
    private final ComputationManager computationManager;
    private final int priority;
    private final DynawoConfig config;
    private static final Logger LOG = LoggerFactory.getLogger(DynawoImpactAnalysis.class);
}
