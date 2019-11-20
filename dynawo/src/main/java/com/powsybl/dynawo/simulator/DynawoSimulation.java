/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.computation.GroupCommandBuilder;
import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynawo.DynawoInputProvider;
import com.powsybl.dynawo.simulator.results.DynawoResults;
import com.powsybl.dynawo.xml.DynawoXmlExporter;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulation {

    private static final String WORKING_DIR_PREFIX = "powsybl_dynawo_impact_analysis_";
    private static final String OUTPUT_FILE = "curves/curves.csv";

    public DynawoSimulation(Network network, ComputationManager computationManager, int priority,
        DynawoInputProvider dynawoProvider) {
        this.network = network;
        this.computationManager = computationManager;
        this.priority = priority;
        this.dynawoProvider = dynawoProvider;
    }

    private Command createCommand(String dynawoJobsFile, DynawoConfig dynawoConfig) {
        return new GroupCommandBuilder()
            .id("dyn_fs")
            .subCommand()
            .program(dynawoConfig.getDynawoCptCommandName())
            .args("jobs", dynawoJobsFile)
            .add()
            .build();
    }

    private Command before(Path workingDir, DynawoConfig dynawoConfig) {
        String dynawoJobsFile = new DynawoXmlExporter().export(network, dynawoProvider, workingDir);
        LOGGER.info("cmd {} jobs {}", dynawoConfig.getDynawoCptCommandName(), dynawoJobsFile);
        return createCommand(dynawoJobsFile, dynawoConfig);
    }

    private DynawoResults after(Path workingDir, ExecutionReport report) {
        String log = null;
        if (!report.getErrors().isEmpty()) {
            report.log();
            String exitCodes = report.getErrors().stream()
                    .map(err -> String.format("Task %d : %d", err.getIndex(), err.getExitCode()))
                    .collect(Collectors.joining(", "));
            log = String.format("Error during the execution in directory  %s exit codes: %s", workingDir.toAbsolutePath(), exitCodes);
        }

        DynawoResults results = new DynawoResults(log == null, log);
        Path file = workingDir.resolve(dynawoProvider.getDynawoJobs(network).get(0).getOutputs().getDirectory())
            .resolve(OUTPUT_FILE);
        try {
            if (file.toFile().exists()) {
                results.parseCsv(file);
            }
        } catch (Exception x) {
            results.setStatus(false);
            results.setLogs(x.toString());
        }
        return results;
    }

    public CompletableFuture<DynamicSimulationResult> run(String workingStateId, DynawoConfig dynawoConfig) {
        return computationManager.execute(
            new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynawoConfig.isDebug()),
            new AbstractExecutionHandler<DynamicSimulationResult>() {

                @Override
                public List<CommandExecution> before(Path workingDir) {
                    network.getVariantManager().setWorkingVariant(workingStateId);
                    Command cmd = DynawoSimulation.this.before(workingDir, dynawoConfig);
                    return Collections.singletonList(new CommandExecution(cmd, 1, priority));
                }

                @Override
                public DynamicSimulationResult after(Path workingDir, ExecutionReport report) {
                    return DynawoSimulation.this.after(workingDir, report);
                }
            });
    }

    private final Network network;
    private final DynawoInputProvider dynawoProvider;
    private final ComputationManager computationManager;
    private final int priority;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoSimulation.class);
}
