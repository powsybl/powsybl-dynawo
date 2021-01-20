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
import com.powsybl.dynawo.xml.JobsXml;
import com.powsybl.dynawo.xml.ParametersXml;
import com.powsybl.iidm.export.Exporters;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.IidmXmlVersion;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeriesConstants;
import com.powsybl.timeseries.TimeSeries.TimeFormat;
import com.powsybl.timeseries.TimeSeriesCsvConfig;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynawo.xml.DynawoConstants.JOBS_FILENAME;
import static com.powsybl.dynawo.xml.DynawoConstants.NETWORK_FILENAME;
import static com.powsybl.dynawo.xml.DynawoConstants.CURVES_OUTPUT_PATH;
import static com.powsybl.dynawo.xml.DynawoConstants.CURVES_FILENAME;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicSimulationProvider.class)
public class DynawoProvider implements DynamicSimulationProvider {

    public static final String NAME = "Dynawo";
    private static final String DYNAWO_CMD_NAME = "dynawo";
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
        return NAME;
    }

    @Override
    public String getVersion() {
        return "1.2.0";
    }

    @Override
    public CompletableFuture<DynamicSimulationResult> run(Network network, DynamicModelsSupplier dynamicModelsSupplier, EventModelsSupplier eventModelsSupplier, CurvesSupplier curvesSupplier, String workingVariantId,
                                                          ComputationManager computationManager, DynamicSimulationParameters parameters) {
        Objects.requireNonNull(dynamicModelsSupplier);
        Objects.requireNonNull(eventModelsSupplier);
        Objects.requireNonNull(curvesSupplier);
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(parameters);

        DynawoParameters dynawoParameters = getDynawoSimulationParameters(parameters);
        return run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier, workingVariantId, computationManager, parameters, dynawoParameters);
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
            Path curvesPath = workingDir.resolve(CURVES_OUTPUT_PATH).toAbsolutePath().resolve(CURVES_FILENAME);
            Map<String, TimeSeries> curves = new HashMap<>();
            if (Files.exists(curvesPath)) {
                Map<Integer, List<TimeSeries>> curvesPerVersion = TimeSeries.parseCsv(curvesPath, new TimeSeriesCsvConfig(TimeSeriesConstants.DEFAULT_SEPARATOR, false, TimeFormat.FRACTIONS_OF_SECOND));
                curvesPerVersion.values().forEach(l -> l.forEach(curve -> curves.put(curve.getMetadata().getName(), curve)));
            }
            return new DynamicSimulationResultImpl(true, null, curves, DynamicSimulationResult.emptyTimeLine());
        }

        private void writeInputFiles(Path workingDir) {
            try {
                // Write the network to XIIDM v1.0 because currently Dynawo only supports this version
                Properties params = new Properties();
                params.setProperty(XMLExporter.VERSION, IidmXmlVersion.V_1_0.toString("."));
                Exporters.export("XIIDM", context.getNetwork(), params, workingDir.resolve(NETWORK_FILENAME));

                JobsXml.write(workingDir, context);
                DydXml.write(workingDir, context);
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
                .args(SystemUtils.IS_OS_WINDOWS ? "--jobs-file" : "jobs", dynawoJobsFile.toString())
                .add()
                .build();
        }

        private String getProgram() {
            String extension = SystemUtils.IS_OS_WINDOWS ? ".cmd" : ".sh";
            return Paths.get(dynawoConfig.getHomeDir()).resolve(DYNAWO_CMD_NAME + extension).toString();
        }
    }
}
