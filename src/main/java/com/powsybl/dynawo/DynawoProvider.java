/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import com.google.auto.service.AutoService;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.computation.*;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynawo.xml.CurvesXml;
import com.powsybl.dynawo.xml.DydXml;
import com.powsybl.dynawo.xml.EventsXml;
import com.powsybl.dynawo.xml.JobsXml;
import com.powsybl.dynawo.xml.ParametersXml;
import com.powsybl.iidm.export.Exporters;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.IidmXmlVersion;
import com.powsybl.iidm.xml.XMLExporter;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynawo.xml.DynawoConstants.JOBS_FILENAME;
import static com.powsybl.dynawo.xml.DynawoConstants.NETWORK_FILENAME;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicSimulationProvider.class)
public class DynawoProvider implements DynamicSimulationProvider {

    private static final String DYNAWO_CMD_NAME = "dynawo.sh";
    private static final String WORKING_DIR_PREFIX = "powsybl_dynawo_";

    private final DynawoConfig dynawoConfig;

    public DynawoProvider() {
        this(DynawoConfig.load());
    }

    public DynawoProvider(DynawoConfig dynawoConfig) {
        this.dynawoConfig = Objects.requireNonNull(dynawoConfig);
    }

    @Override
    public String getName() {
        return "Dynawo";
    }

    @Override
    public String getVersion() {
        return "1.2.0";
    }

    @Override
    public CompletableFuture<DynamicSimulationResult> run(Network network, DynamicModelsSupplier dynamicModelsSupplier, EventModelsSupplier eventsModelsSupplier, CurvesSupplier curvesSupplier,
                                                          String workingVariantId, ComputationManager computationManager, DynamicSimulationParameters parameters) {
        Objects.requireNonNull(dynamicModelsSupplier);
        Objects.requireNonNull(eventsModelsSupplier);
        Objects.requireNonNull(curvesSupplier);
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(parameters);

        DynawoParameters dynawoParameters = getDynawoSimulationParameters(parameters);
        return run(network, dynamicModelsSupplier, eventsModelsSupplier, curvesSupplier, workingVariantId, computationManager, parameters, dynawoParameters);
    }

    private DynawoParameters getDynawoSimulationParameters(DynamicSimulationParameters parameters) {
        DynawoParameters dynawoParameters = parameters.getExtension(DynawoParameters.class);
        if (dynawoParameters == null) {
            dynawoParameters = DynawoParameters.load();
        }
        return dynawoParameters;
    }

    private CompletableFuture<DynamicSimulationResult> run(Network network, DynamicModelsSupplier dynamicModelsSupplier, EventModelsSupplier eventsModelsSupplier, CurvesSupplier curvesSupplier,
                                                           String workingVariantId, ComputationManager computationManager, DynamicSimulationParameters parameters, DynawoParameters dynawoParameters) {

        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynawoConfig.isDebug());

        DynawoContext context = new DynawoContext(network, dynamicModelsSupplier.get(network), eventsModelsSupplier.get(network), curvesSupplier.get(network), parameters, dynawoParameters);
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
                // Write the network to XIIDM v1.0 because currently Dynawo only supports this version
                Properties params = new Properties();
                params.setProperty(XMLExporter.VERSION, IidmXmlVersion.V_1_0.toString("."));
                Exporters.export("XIIDM", context.getNetwork(), params, workingDir.resolve(NETWORK_FILENAME));

                JobsXml.write(workingDir, context);
                DydXml.write(workingDir, context);
                EventsXml.write(workingDir, context);
                ParametersXml.write(workingDir, context);
                if (context.withCurves()) {
                    CurvesXml.write(workingDir, context);
                }
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
            return Paths.get(dynawoConfig.getHomeDir()).resolve(DYNAWO_CMD_NAME).toString();
        }
    }
}
