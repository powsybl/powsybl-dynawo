/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.FileUtils;
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
import com.powsybl.dynawo.simulator.input.DynawoInputs;
import com.powsybl.dynawo.simulator.results.DynawoResults;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.simulation.ImpactAnalysis;
import com.powsybl.simulation.ImpactAnalysisProgressListener;
import com.powsybl.simulation.ImpactAnalysisResult;
import com.powsybl.simulation.SimulationParameters;
import com.powsybl.simulation.SimulationState;

import static com.powsybl.dynawo.simulator.DynawoConstants.JOBS_FILENAME;
import static com.powsybl.dynawo.simulator.DynawoConstants.SOLVER_PAR_FILENAME;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoImpactAnalysis implements ImpactAnalysis {

    private static final String WORKING_DIR_PREFIX = "powsybl_dynawo_impact_analysis_";
    private static final String OUTPUT_FILE = "outputs/curves/curves.csv";
    private static final String DEFAULT_DYNAWO_CASE_NAME = "nrt/data/IEEE14/IEEE14_BasicTestCases/IEEE14_DisconnectLine/IEEE14.jobs";

    public DynawoImpactAnalysis(Network network, ComputationManager computationManager, int priority) {
        this(network, computationManager, priority, new XMLExporter(), DynawoConfig.load());
    }

    public DynawoImpactAnalysis(Network network, ComputationManager computationManager, int priority,
        XMLExporter xmlExporter, DynawoConfig dynawoConfig) {
        this.network = network;
        this.computationManager = computationManager;
        this.priority = priority;
        this.xmlExporter = xmlExporter;
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
        String dynawoJobsFile = DEFAULT_DYNAWO_CASE_NAME;
        copyResource(workingDir, JOBS_FILENAME);
        copyResource(workingDir, SOLVER_PAR_FILENAME);
        try {
            DynawoInputs.prepare(network, workingDir);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        if (network != null) {
            Path jobsFile = workingDir.resolve("dynawoModel.jobs");
            Properties params = new Properties();
            params.put("iidm.export.xml.extensions", "null");
            xmlExporter.export(network, params, new FileDataSource(workingDir, "dynawoModel"));
            // Error in dynawo because substation is exported without country field
            dynawoJobsFile = jobsFile.toAbsolutePath().toString();
        }
        LOGGER.info("cmd {} jobs {}", dynawoConfig.getDynawoCptCommandName(), dynawoJobsFile);
        return createCommand(dynawoJobsFile);
    }

    private void copyResource(Path workingDir, String fileName) {
        try {
            File destination = new File(workingDir.toString(), fileName);
            FileUtils.copyURLToFile(getClass().getResource("/nordic32/" + fileName), destination);
        } catch (IOException e) {
            LOGGER.error("copying resource {}", fileName);
        }
    }

    private ImpactAnalysisResult after(Path workingDir, ExecutionReport report) {
        report.log();

        Map<String, String> metrics = new HashMap<>();
        metrics.put("success", report.getErrors().isEmpty() ? "true" : "false");
        DynawoResults results = new DynawoResults(metrics);
        Path file = workingDir.resolve(OUTPUT_FILE);
        try {
            if (file.toFile().exists()) {
                results.parseCsv(file);
            }
        } catch (Exception x) {
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
    private final ComputationManager computationManager;
    private final int priority;
    private final DynawoConfig dynawoConfig;
    private final XMLExporter xmlExporter;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoImpactAnalysis.class);
}
