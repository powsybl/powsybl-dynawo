/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.powsybl.dynawo.inputs.dsl.GroovyDslDynawoInputProviderFactory;
import com.powsybl.dynawo.inputs.model.DynawoInputs;
import com.powsybl.dynawo.inputs.model.DynawoInputsProvider;
import com.powsybl.dynawo.inputs.model.job.Solver;
import com.powsybl.dynawo.inputs.xml.DynawoInputsXmlExporter;
import com.powsybl.dynawo.results.CurvesCsv;
import com.powsybl.dynawo.results.DynawoResults;
import com.powsybl.dynawo.results.TimeSeries;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverIDAParameters;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicSimulationProvider.class)
public class DynawoSimulationProvider implements DynamicSimulationProvider {

    private static final String DEFAULT_DYNAWO_CMD_NAME = "myEnvDynawo.sh";
    private static final String WORKING_DIR_PREFIX = "powsybl_dynawo_";
    private static final String OUTPUT_FILE = "curves/curves.csv";

    public DynawoSimulationProvider() {
        this(DynawoConfig.load());
    }

    public DynawoSimulationProvider(DynawoConfig dynawoConfig) {
        this.dynawoConfig = Objects.requireNonNull(dynawoConfig);
    }

    @Override
    public String getName() {
        return "DynawoSimulation";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public CompletableFuture<DynamicSimulationResult> run(Network network, ComputationManager computationManager,
        String workingVariantId,
        DynamicSimulationParameters parameters) {
        DynawoSimulationParameters dynawoParameters = parameters.getExtensionByName("DynawoSimulationParameters");
        if (dynawoParameters == null) {
            dynawoParameters = new DynawoSimulationParameters();
        }
        return run(network, computationManager, workingVariantId, parameters, dynawoParameters);
    }

    private CompletableFuture<DynamicSimulationResult> run(Network network, ComputationManager computationManager,
        String workingStateId, DynamicSimulationParameters parameters, DynawoSimulationParameters dynawoParameters) {

        network.getVariantManager().setWorkingVariant(workingStateId);
        DynawoInputs inputs = prepareDynawoInputs(network, parameters, dynawoParameters);

        return computationManager.execute(
            new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynawoConfig.isDebug()),
            new AbstractExecutionHandler<DynamicSimulationResult>() {

                @Override
                public List<CommandExecution> before(Path workingDir) {
                    Path dynawoJobsFile = writeInputFiles(inputs, workingDir);
                    Command cmd = createCommand(dynawoJobsFile);
                    return Collections.singletonList(new CommandExecution(cmd, 1));
                }

                @Override
                public DynamicSimulationResult after(Path workingDir, ExecutionReport report) throws IOException {
                    super.after(workingDir, report);
                    return results(inputs, workingDir, report);
                }

            });
    }

    private static DynawoInputs prepareDynawoInputs(Network network, DynamicSimulationParameters parameters, DynawoSimulationParameters dynawoParameters) {
        // FIXME currently, the only way to obtain Dynawo inputs
        // is receiving then directly from the parameters (used in tests)
        // or reading from Groovy DSL files
        DynawoInputs inputs = dynawoParameters.getDynawoInputs();
        if (inputs == null) {
            if (dynawoParameters.getDslFilename() == null) {
                throw new PowsyblException("Unable to obtain Dynawo inputs. No explicit inputs given. No dslFilename in Dynawo parameters");
            }
            Path dslFile = Paths.get(dynawoParameters.getDslFilename());
            DynawoInputsProvider in = new GroovyDslDynawoInputProviderFactory().create(dslFile);
            inputs = in.getDynawoInputs(network);
        }

        // FIXME Complete the Dynawo inputs with additional information
        // received through simulation parameters
        // We should only complete missing information in the inputs
        // As an example: each job may use a different solver
        // Some jobs could have its solver already configured
        // For the rest of jobs that do not define a solver,
        // we assign it the solver defined in the parameters
        inputs.getJobs().forEach(j -> {
            if (j.getSolver() == null) {
                String lib;
                String parFile;
                String parId;
                switch (dynawoParameters.getSolverParameters().getType()) {
                    case IDA:
                        lib = "dynawo_SolverIDA";
                        // FIXME We should create a parId in the simulation par file
                        // and write on it the parameter "order" with its value
                        LOG.warn("PENDING adding IDA order {} to simulation parFile", ((SolverIDAParameters) dynawoParameters.getSolverParameters()).getOrder());
                        parFile = "pending";
                        parId = "pending";
                        LOG.warn("PENDING define parFile ({}) and parId ({})", parFile, parId);
                        break;
                    case SIM:
                    default:
                        lib = "dynawo_SolverSIM";
                        parFile = null;
                        parId = null;
                        break;
                }
                j.setSolver(new Solver(lib, parFile, parId));
            }
        });
        return inputs;
    }

    private static Path writeInputFiles(DynawoInputs inputs, Path workingDir) {
        try {
            return new DynawoInputsXmlExporter().export(inputs, workingDir);
        } catch (IOException | XMLStreamException e) {
            throw new PowsyblException(e.getMessage());
        }
    }

    private Command createCommand(Path dynawoJobsFile) {
        return new GroupCommandBuilder()
            .id("dyn_fs")
            .subCommand()
            .program(getProgram())
            .args("jobs", dynawoJobsFile.toAbsolutePath().toString())
            .add()
            .build();
    }

    private String getProgram() {
        return dynawoConfig.getHomeDir().endsWith("/") ? dynawoConfig.getHomeDir() + DEFAULT_DYNAWO_CMD_NAME
            : dynawoConfig.getHomeDir() + "/" + DEFAULT_DYNAWO_CMD_NAME;
    }

    private DynawoResults results(DynawoInputs inputs, Path workingDir, ExecutionReport report) {
        // Initially results are ok if the command was completed without errors
        boolean isOk = report.getErrors().isEmpty();
        // We do not consider the logs
        String logs = null;
        DynawoResults results = new DynawoResults(isOk, logs);

        // FIXME Only the results of the first job are read
        Path file = workingDir
            .resolve(inputs.getJobs().get(0).getOutputs().getDirectory())
            .resolve(OUTPUT_FILE);
        try {
            TimeSeries timeSeries = CurvesCsv.parse(file);
            results.setTimeSeries(timeSeries);
        } catch (PowsyblException x) {
            results.setOk(false);
            results.setLogs(x.toString());
        }
        return results;
    }

    private final DynawoConfig dynawoConfig;
    private static final Logger LOG = LoggerFactory.getLogger(DynawoSimulationProvider.class);
}
