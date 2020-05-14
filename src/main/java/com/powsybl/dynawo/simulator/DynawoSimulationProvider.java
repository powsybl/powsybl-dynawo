/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.xml.stream.XMLStreamException;

import com.google.auto.service.AutoService;
import com.powsybl.commons.datasource.FileDataSource;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
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
import com.powsybl.dynamicsimulation.DynamicSimulationResultImpl;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.xml.DynawoConstants;
import com.powsybl.dynawo.xml.JobsXml;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.XMLExporter;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicSimulationProvider.class)
public class DynawoSimulationProvider implements DynamicSimulationProvider {

    private static final String DYNAWO_CMD_NAME = "execDynawo.sh";
    private static final String WORKING_DIR_PREFIX = "powsybl_dynawo_";

    private final DynawoConfig dynawoConfig;

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
    public CompletableFuture<DynamicSimulationResult> run(Network network, ComputationManager computationManager, String workingVariantId, DynamicSimulationParameters parameters) {
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(parameters);

        DynawoSimulationParameters dynawoParameters = getDynawoSimulationParameters(parameters);
        return run(network, computationManager, workingVariantId, parameters, dynawoParameters);
    }

    private DynawoSimulationParameters getDynawoSimulationParameters(DynamicSimulationParameters parameters) {
        DynawoSimulationParameters dynawoParameters = parameters.getExtension(DynawoSimulationParameters.class);
        if (dynawoParameters == null) {
            dynawoParameters = DynawoSimulationParameters.load();
            parameters.addExtension(DynawoSimulationParameters.class, dynawoParameters);
        }
        return dynawoParameters;
    }

    private CompletableFuture<DynamicSimulationResult> run(Network network, ComputationManager computationManager,
        String workingStateId, DynamicSimulationParameters parameters, DynawoSimulationParameters dynawoParameters) {

        network.getVariantManager().setWorkingVariant(workingStateId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynawoConfig.isDebug());

        return computationManager.execute(execEnv, new DynawoHandler(network, parameters, dynawoParameters));
    }

    private final class DynawoHandler extends AbstractExecutionHandler<DynamicSimulationResult> {

        private final Network network;
        private final DynamicSimulationParameters parameters;
        private final DynawoSimulationParameters dynawoParameters;

        public DynawoHandler(Network network, DynamicSimulationParameters parameters, DynawoSimulationParameters dynawoParameters) {
            this.network = network;
            this.parameters = parameters;
            this.dynawoParameters = dynawoParameters;
        }

        @Override
        public List<CommandExecution> before(Path workingDir) {
            writeInputFiles(workingDir, network, parameters);
            Command cmd = createCommand(workingDir.resolve(DynawoConstants.JOBS_FILENAME));
            return Collections.singletonList(new CommandExecution(cmd, 1));
        }

        @Override
        public DynamicSimulationResult after(Path workingDir, ExecutionReport report) throws IOException {
            super.after(workingDir, report);
            return new DynamicSimulationResultImpl(true, "");
        }

        private void writeInputFiles(Path workingDir, Network network, DynamicSimulationParameters parameters) {
            DynawoContext context = new DynawoContext(network, parameters);
            try {
                new XMLExporter().export(network, null, new FileDataSource(workingDir, DynawoConstants.BASEFILENAME));
                JobsXml.write(workingDir, context);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (XMLStreamException e) {
                throw new UncheckedXmlStreamException(e);
            }
        }

        private Command createCommand(Path dynawoJobsFile) {
            return new GroupCommandBuilder()
                .id("dyn_fs")
                .subCommand()
                .program(getProgram())
                .args("jobs", dynawoJobsFile.toString())
                .add()
                .build();
        }

        private String getProgram() {
            return Paths.get(dynawoConfig.getHomeDir()).resolve("bin").resolve(DYNAWO_CMD_NAME).toString();
        }
    }
}
