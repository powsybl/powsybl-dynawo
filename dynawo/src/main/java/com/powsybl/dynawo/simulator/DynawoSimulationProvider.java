/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import com.google.auto.service.AutoService;
import com.powsybl.commons.PowsyblException;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.computation.GroupCommandBuilder;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.DynamicSimulationProvider;
import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynawo.simulator.results.DynawoResults;
import com.powsybl.dynawo.xml.DynawoXmlExporter;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicSimulationProvider.class)
public class DynawoSimulationProvider implements DynamicSimulationProvider {

    private static final String WORKING_DIR_PREFIX = "powsybl_dynawo_";
    private static final String OUTPUT_FILE = "curves/curves.csv";

    @Override
    public String getName() {
        return "DynawoSimulation";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    private Command createCommand(DynamicSimulationParameters parameters, DynawoParameters dynawoConfig, String dynawoJobsFile) {
        return new GroupCommandBuilder()
            .id("dyn_fs")
            .subCommand()
            .program(dynawoConfig.getDynawoCommandName())
            .args("jobs", dynawoJobsFile)
            .add()
            .build();
    }

    private DynawoResults results(Network network, DynamicSimulationParameters parameters, DynawoParameters dynawoParameters,
        Path workingDir, ExecutionReport report) {
        String log = null;
        if (!report.getErrors().isEmpty()) {
            report.log();
            String exitCodes = report.getErrors().stream()
                .map(err -> String.format("Task %d : %d", err.getIndex(), err.getExitCode()))
                .collect(Collectors.joining(", "));
            log = String.format("Error during the execution in directory  %s exit codes: %s",
                workingDir.toAbsolutePath(), exitCodes);
        }

        DynawoResults results = new DynawoResults(log == null, log);
        Path file = workingDir.resolve(dynawoParameters.getDynawoInputProvider().getDynawoJobs(network).get(0).getOutputs().getDirectory())
            .resolve(OUTPUT_FILE);
        try {
            results.parseCsv(file);
        } catch (PowsyblException x) {
            results.setStatus(false);
            results.setLogs(x.toString());
        }
        return results;
    }

    private CompletableFuture<DynamicSimulationResult> run(Network network, ComputationManager computationManager,
        String workingStateId, DynamicSimulationParameters parameters, DynawoParameters dynawoParameters) {
        return computationManager.execute(
            new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynawoParameters.isDebug()),
            new AbstractExecutionHandler<DynamicSimulationResult>() {

                @Override
                public List<CommandExecution> before(Path workingDir) {
                    network.getVariantManager().setWorkingVariant(workingStateId);
                    String dynawoJobsFile = "";
                    try {
                        dynawoJobsFile = new DynawoXmlExporter().export(network, dynawoParameters.getDynawoInputProvider(), workingDir);
                    } catch (IOException | XMLStreamException e) {
                        throw new PowsyblException(e.getMessage());
                    }
                    Command cmd = createCommand(parameters, dynawoParameters, dynawoJobsFile);
                    return Collections.singletonList(new CommandExecution(cmd, 1));
                }

                @Override
                public DynamicSimulationResult after(Path workingDir, ExecutionReport report) {
                    return results(network, parameters, dynawoParameters, workingDir, report);
                }
            });
    }

    @Override
    public CompletableFuture<DynamicSimulationResult> run(Network network, ComputationManager computationManager, String workingVariantId,
        DynamicSimulationParameters parameters) {
        DynawoParameters dynawoParameters = parameters.getExtensionByName("DynawoConfig");
        if (dynawoParameters == null) {
            dynawoParameters = new DynawoParameters();
        }
        return run(network, computationManager, workingVariantId, parameters, dynawoParameters);
    }

}
