/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import com.google.auto.service.AutoService;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.computation.*;
import com.powsybl.dynamicsimulation.CurvesSupplier;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.DynamicSimulationProvider;
import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynamicsimulation.DynamicSimulationResultImpl;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.xml.CurvesXml;
import com.powsybl.dynawo.xml.JobsXml;
import com.powsybl.iidm.export.Exporters;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.IidmXmlVersion;
import com.powsybl.iidm.xml.XMLExporter;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynawo.xml.DynawoConstants.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicSimulationProvider.class)
public class DynawoSimulationProvider implements DynamicSimulationProvider {

    private static final String DYNAWO_CMD_NAME = "execDynawo.sh";
    private static final String WORKING_DIR_PREFIX = "powsybl_dynawo_";

    private final DynawoConfig dynawoConfig;

    // FIXME(mathbagu): to be removed once the DYD file will be generated
    private String dydFilename;

    public DynawoSimulationProvider() {
        this(DynawoConfig.load());
    }

    public DynawoSimulationProvider(DynawoConfig dynawoConfig) {
        this.dynawoConfig = Objects.requireNonNull(dynawoConfig);
    }

    // FIXME(mathbagu): to be removed once the DYD file will be generated
    public void setDydFilename(String dydFile) {
        this.dydFilename = Objects.requireNonNull(dydFile);
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
    public CompletableFuture<DynamicSimulationResult> run(Network network, CurvesSupplier curvesSupplier, String workingVariantId, 
                                                          ComputationManager computationManager, DynamicSimulationParameters parameters) {
        Objects.requireNonNull(curvesSupplier);
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(parameters);

        DynawoSimulationParameters dynawoParameters = getDynawoSimulationParameters(parameters);
        return run(network, curvesSupplier, workingVariantId, computationManager, parameters, dynawoParameters);
    }

    private DynawoSimulationParameters getDynawoSimulationParameters(DynamicSimulationParameters parameters) {
        DynawoSimulationParameters dynawoParameters = parameters.getExtension(DynawoSimulationParameters.class);
        if (dynawoParameters == null) {
            dynawoParameters = DynawoSimulationParameters.load();
        }
        return dynawoParameters;
    }

    private CompletableFuture<DynamicSimulationResult> run(Network network, CurvesSupplier curvesSupplier, String workingVariantId, 
                                                           ComputationManager computationManager, DynamicSimulationParameters parameters, DynawoSimulationParameters dynawoParameters) {

        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynawoConfig.isDebug());

        DynawoContext context = new DynawoContext(network, curvesSupplier.get(network), parameters, dynawoParameters);
        return computationManager.execute(execEnv, new DynawoHandler(context));
    }

    private final class DynawoHandler extends AbstractExecutionHandler<DynamicSimulationResult> {

        private final DynawoContext context;

        public DynawoHandler(DynawoContext context) {
            this.context = context;
        }

        @Override
        public List<CommandExecution> before(Path workingDir) {
            writeInputFiles(workingDir);
            Command cmd = createCommand(workingDir.resolve(JOBS_FILENAME));
            return Collections.singletonList(new CommandExecution(cmd, 1));
        }

        @Override
        public DynamicSimulationResult after(Path workingDir, ExecutionReport report) throws IOException {
            super.after(workingDir, report);
            return new DynamicSimulationResultImpl(true, "");
        }

        private void writeInputFiles(Path workingDir) {
            try {
                // FIXME(mathbagu): To be refactored
                if (dydFilename != null) {
                    Files.copy(Paths.get(dydFilename), workingDir.resolve(DYD_FILENAME));
                }

                writeParametersFiles(workingDir);

                // Write the network to XIIDM v1.0 because currently Dynawo only supports this version
                Properties params = new Properties();
                params.setProperty(XMLExporter.VERSION, IidmXmlVersion.V_1_0.toString("."));
                Exporters.export("XIIDM", context.getNetwork(), params, workingDir.resolve(NETWORK_FILENAME));

                JobsXml.write(workingDir, context);
                if (context.withCurves()) {
                    CurvesXml.write(workingDir, context);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (XMLStreamException e) {
                throw new UncheckedXmlStreamException(e);
            }
        }

        private void writeParametersFiles(Path workingDirectory) throws IOException {
            Path parametersFile = Paths.get(context.getDynawoParameters().getParametersFile());
            if (Files.exists(parametersFile)) {
                Files.copy(parametersFile, workingDirectory.resolve(parametersFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }

            Path networkParFile = Paths.get(context.getDynawoParameters().getNetwork().getParametersFile());
            if (Files.exists(networkParFile)) {
                Files.copy(networkParFile, workingDirectory.resolve(networkParFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }

            Path solverParFile = Paths.get(context.getDynawoParameters().getSolver().getParametersFile());
            if (Files.exists(solverParFile)) {
                Files.copy(solverParFile, workingDirectory.resolve(solverParFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
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
