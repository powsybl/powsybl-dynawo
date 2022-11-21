/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.google.auto.service.AutoService;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.computation.*;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynawaltz.xml.CurvesXml;
import com.powsybl.dynawaltz.xml.DydXml;
import com.powsybl.dynawaltz.xml.JobsXml;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.dynawo.commons.DynawoResultsNetworkUpdate;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.IidmXmlVersion;
import com.powsybl.iidm.xml.NetworkXml;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeries.TimeFormat;
import com.powsybl.timeseries.TimeSeriesConstants;
import com.powsybl.timeseries.TimeSeriesCsvConfig;
import org.apache.commons.lang3.SystemUtils;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicSimulationProvider.class)
public class DynaWaltzProvider implements DynamicSimulationProvider {

    public static final String NAME = "DynaWaltz";
    private static final String DYNAWO_CMD_NAME = "dynawo";
    private static final String WORKING_DIR_PREFIX = "powsybl_dynawaltz_";
    private static final String OUTPUT_IIDM_FILENAME = "outputIIDM.xml";
    private static final String IIDM_VERSION = IidmXmlVersion.V_1_4.toString(".");

    private final DynaWaltzConfig dynaWaltzConfig;

    public DynaWaltzProvider() {
        this(DynaWaltzConfig.load());
    }

    public DynaWaltzProvider(DynaWaltzConfig dynawoConfig) {
        this.dynaWaltzConfig = Objects.requireNonNull(dynawoConfig);
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

        DynaWaltzParameters dynaWaltzParameters = getDynaWaltzSimulationParameters(parameters);
        return run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier, workingVariantId, computationManager, parameters, dynaWaltzParameters);
    }

    private DynaWaltzParameters getDynaWaltzSimulationParameters(DynamicSimulationParameters parameters) {
        DynaWaltzParameters dynaWaltzParameters = parameters.getExtension(DynaWaltzParameters.class);
        if (dynaWaltzParameters == null) {
            dynaWaltzParameters = DynaWaltzParameters.load();
        }
        return dynaWaltzParameters;
    }

    private CompletableFuture<DynamicSimulationResult> run(Network network, DynamicModelsSupplier dynamicModelsSupplier, EventModelsSupplier eventsModelsSupplier, CurvesSupplier curvesSupplier,
                                                           String workingVariantId, ComputationManager computationManager, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters) {

        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynaWaltzConfig.isDebug());

        DynaWaltzContext context = new DynaWaltzContext(network, workingVariantId, dynamicModelsSupplier.get(network), eventsModelsSupplier.get(network), curvesSupplier.get(network), parameters, dynaWaltzParameters);
        return computationManager.execute(execEnv, new DynaWaltzHandler(context));
    }

    private final class DynaWaltzHandler extends AbstractExecutionHandler<DynamicSimulationResult> {

        private final DynaWaltzContext context;

        public DynaWaltzHandler(DynaWaltzContext context) {
            this.context = context;
        }

        @Override
        public List<CommandExecution> before(Path workingDir) throws IOException {
            Path outputNetworkFile = workingDir.resolve("outputs").resolve("finalState").resolve(OUTPUT_IIDM_FILENAME);
            if (Files.exists(outputNetworkFile)) {
                Files.delete(outputNetworkFile);
            }
            Path curvesPath = workingDir.resolve(CURVES_OUTPUT_PATH).toAbsolutePath().resolve(CURVES_FILENAME);
            if (Files.exists(curvesPath)) {
                Files.delete(curvesPath);
            }
            writeInputFiles(workingDir);
            Command cmd = createCommand(workingDir.resolve(JOBS_FILENAME));
            return Collections.singletonList(new CommandExecution(cmd, 1));
        }

        @Override
        public DynamicSimulationResult after(Path workingDir, ExecutionReport report) throws IOException {
            super.after(workingDir, report);
            context.getNetwork().getVariantManager().setWorkingVariant(context.getWorkingVariantId());
            boolean status = true;
            Path outputNetworkFile = workingDir.resolve("outputs").resolve("finalState").resolve(OUTPUT_IIDM_FILENAME);
            if (Files.exists(outputNetworkFile)) {
                DynawoResultsNetworkUpdate.update(context.getNetwork(), NetworkXml.read(outputNetworkFile));
            } else {
                status = false;
            }
            Path curvesPath = workingDir.resolve(CURVES_OUTPUT_PATH).toAbsolutePath().resolve(CURVES_FILENAME);
            Map<String, TimeSeries> curves = new HashMap<>();
            if (Files.exists(curvesPath)) {
                Map<Integer, List<TimeSeries>> curvesPerVersion = TimeSeries.parseCsv(curvesPath, new TimeSeriesCsvConfig(TimeSeriesConstants.DEFAULT_SEPARATOR, false, TimeFormat.FRACTIONS_OF_SECOND));
                curvesPerVersion.values().forEach(l -> l.forEach(curve -> curves.put(curve.getMetadata().getName(), curve)));
            } else {
                if (context.withCurves()) {
                    status = false;
                }
            }
            return new DynamicSimulationResultImpl(status, null, curves, DynamicSimulationResult.emptyTimeLine());
        }

        private void writeInputFiles(Path workingDir) {
            try {
                Properties params = new Properties();
                params.setProperty(XMLExporter.VERSION, IIDM_VERSION);
                context.getNetwork().write("XIIDM", params, workingDir.resolve(NETWORK_FILENAME));

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
            return Paths.get(dynaWaltzConfig.getHomeDir()).resolve(DYNAWO_CMD_NAME + extension).toString();
        }
    }
}
