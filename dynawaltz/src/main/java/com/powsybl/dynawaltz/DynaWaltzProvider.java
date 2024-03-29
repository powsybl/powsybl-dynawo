/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.google.auto.service.AutoService;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.*;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.xml.CurvesXml;
import com.powsybl.dynawaltz.xml.DydXml;
import com.powsybl.dynawaltz.xml.JobsXml;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.dynawo.commons.*;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.dynawo.commons.dynawologs.CsvLogParser;
import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.dynawo.commons.timeline.CsvTimeLineParser;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.serde.NetworkSerDe;
import com.powsybl.timeseries.DoubleTimeSeries;
import com.powsybl.timeseries.TimeSeries;
import com.powsybl.timeseries.TimeSeries.TimeFormat;
import com.powsybl.timeseries.TimeSeriesConstants;
import com.powsybl.timeseries.TimeSeriesCsvConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.*;
import static com.powsybl.dynawo.commons.DynawoConstants.DYNAWO_TIMELINE_FOLDER;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
@AutoService(DynamicSimulationProvider.class)
public class DynaWaltzProvider implements DynamicSimulationProvider {

    public static final String NAME = "DynaWaltz";
    private static final String WORKING_DIR_PREFIX = "powsybl_dynawaltz_";

    private static final String OUTPUTS_FOLDER = "outputs";
    private static final String FINAL_STATE_FOLDER = "finalState";
    private static final String LOGS_FOLDER = "logs";
    private static final String OUTPUT_IIDM_FILENAME = "outputIIDM.xml";
    private static final String OUTPUT_DUMP_FILENAME = "outputState.dmp";
    private static final String TIMELINE_FILENAME = "timeline.log";
    private static final String LOGS_FILENAME = "dynawaltz.log";
    private static final String ERROR_FILENAME = "dyn_fs_0.err";
    private static final String DYNAWO_ERROR_PATTERN = "DYN Error: ";

    private static final Logger LOGGER = LoggerFactory.getLogger(DynaWaltzProvider.class);

    private final DynaWaltzConfig dynaWaltzConfig;

    public DynaWaltzProvider() {
        this(PlatformConfig.defaultConfig());
    }

    public DynaWaltzProvider(PlatformConfig platformConfig) {
        this(DynaWaltzConfig.load(platformConfig));
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
        return new PowsyblDynawoVersion().getMavenProjectVersion();
    }

    public static Command getCommand(DynaWaltzConfig dynaWaltzConfig) {
        return new GroupCommandBuilder()
                .id("dyn_fs")
                .subCommand()
                .program(dynaWaltzConfig.getProgram())
                .args("jobs", JOBS_FILENAME)
                .add()
                .build();
    }

    public static Command getVersionCommand(DynaWaltzConfig dynaWaltzConfig) {
        List<String> args = Collections.singletonList("version");
        return new SimpleCommandBuilder()
                .id("dynawo_version")
                .program(dynaWaltzConfig.getProgram())
                .args(args)
                .build();
    }

    @Override
    public CompletableFuture<DynamicSimulationResult> run(Network network, DynamicModelsSupplier dynamicModelsSupplier, EventModelsSupplier eventModelsSupplier, CurvesSupplier curvesSupplier, String workingVariantId,
                                                          ComputationManager computationManager, DynamicSimulationParameters parameters, ReportNode reportNode) {
        Objects.requireNonNull(dynamicModelsSupplier);
        Objects.requireNonNull(eventModelsSupplier);
        Objects.requireNonNull(curvesSupplier);
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(reportNode);
        DynaWaltzParameters dynaWaltzParameters = getDynaWaltzSimulationParameters(parameters);
        return run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier, workingVariantId, computationManager, parameters, dynaWaltzParameters, reportNode);
    }

    private DynaWaltzParameters getDynaWaltzSimulationParameters(DynamicSimulationParameters parameters) {
        DynaWaltzParameters dynaWaltzParameters = parameters.getExtension(DynaWaltzParameters.class);
        if (dynaWaltzParameters == null) {
            dynaWaltzParameters = DynaWaltzParameters.load();
        }
        return dynaWaltzParameters;
    }

    private CompletableFuture<DynamicSimulationResult> run(Network network, DynamicModelsSupplier dynamicModelsSupplier, EventModelsSupplier eventsModelsSupplier, CurvesSupplier curvesSupplier,
                                                           String workingVariantId, ComputationManager computationManager, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters, ReportNode reportNode) {

        ReportNode dsReportNode = DynawaltzReports.createDynaWaltzReportNode(reportNode, network.getId());
        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynaWaltzConfig.isDebug());
        Command versionCmd = getVersionCommand(dynaWaltzConfig);
        DynawoUtil.requireDynaMinVersion(execEnv, computationManager, versionCmd, DynawoConstants.DYNAWO_CMD_NAME, false);

        List<BlackBoxModel> blackBoxModels = dynamicModelsSupplier.get(network, dsReportNode).stream()
                .filter(BlackBoxModel.class::isInstance)
                .map(BlackBoxModel.class::cast)
                .collect(Collectors.toList());
        List<BlackBoxModel> blackBoxEventModels = eventsModelsSupplier.get(network, dsReportNode).stream()
                .filter(BlackBoxModel.class::isInstance)
                .map(BlackBoxModel.class::cast)
                .collect(Collectors.toList());
        DynaWaltzContext context = new DynaWaltzContext(network, workingVariantId, blackBoxModels, blackBoxEventModels, curvesSupplier.get(network, dsReportNode), parameters, dynaWaltzParameters, reportNode);
        return computationManager.execute(execEnv, new DynaWaltzHandler(context, reportNode));
    }

    private final class DynaWaltzHandler extends AbstractExecutionHandler<DynamicSimulationResult> {

        private final DynaWaltzContext context;
        private final Network dynawoInput;
        private final ReportNode reportNode;

        private final List<TimelineEvent> timeline = new ArrayList<>();
        private final Map<String, DoubleTimeSeries> curves = new HashMap<>();
        private DynamicSimulationResult.Status status = DynamicSimulationResult.Status.SUCCESS;
        private String statusText = "";

        public DynaWaltzHandler(DynaWaltzContext context, ReportNode reportNode) {
            this.context = context;
            this.dynawoInput = context.getDynaWaltzParameters().isMergeLoads()
                    ? LoadsMerger.mergeLoads(context.getNetwork())
                    : context.getNetwork();
            this.reportNode = reportNode;
        }

        @Override
        public List<CommandExecution> before(Path workingDir) throws IOException {
            Path outputNetworkFile = workingDir.resolve(OUTPUTS_FOLDER).resolve(FINAL_STATE_FOLDER).resolve(OUTPUT_IIDM_FILENAME);
            if (Files.exists(outputNetworkFile)) {
                Files.delete(outputNetworkFile);
            }
            Path curvesPath = workingDir.resolve(CURVES_OUTPUT_PATH).toAbsolutePath().resolve(CURVES_FILENAME);
            if (Files.exists(curvesPath)) {
                Files.delete(curvesPath);
            }
            writeInputFiles(workingDir);
            Command cmd = getCommand(dynaWaltzConfig);
            return Collections.singletonList(new CommandExecution(cmd, 1));
        }

        @Override
        public DynamicSimulationResult after(Path workingDir, ExecutionReport report) throws IOException {

            Path outputsFolder = workingDir.resolve(OUTPUTS_FOLDER);
            context.getNetwork().getVariantManager().setWorkingVariant(context.getWorkingVariantId());
            DynaWaltzParameters parameters = context.getDynaWaltzParameters();
            DumpFileParameters dumpFileParameters = parameters.getDumpFileParameters();

            setDynawoLog(outputsFolder);
            // Error file
            Path errorFile = workingDir.resolve(ERROR_FILENAME);
            if (Files.exists(errorFile)) {
                Matcher errorMatcher = Pattern.compile(DYNAWO_ERROR_PATTERN + "(.*)")
                        .matcher(Files.readString(errorFile));
                if (!errorMatcher.find()) {
                    if (parameters.isWriteFinalState()) {
                        updateNetwork(outputsFolder);
                    }
                    if (dumpFileParameters.exportDumpFile()) {
                        setDumpFile(outputsFolder, dumpFileParameters.dumpFileFolder(), workingDir.getFileName());
                    }
                    setTimeline(outputsFolder);
                    if (context.withCurves()) {
                        setCurves(workingDir);
                    }
                } else {
                    status = DynamicSimulationResult.Status.FAILURE;
                    statusText = errorMatcher.group().substring(DYNAWO_ERROR_PATTERN.length());
                }
            } else {
                LOGGER.warn("Error file not found");
                status = DynamicSimulationResult.Status.FAILURE;
                statusText = "Dynawo error log file not found";
            }

            return new DynamicSimulationResultImpl(status, statusText, curves, timeline);
        }

        private void setDynawoLog(Path outputsFolder) {
            Path logFile = outputsFolder.resolve(LOGS_FOLDER).resolve(LOGS_FILENAME);
            if (Files.exists(logFile)) {
                ReportNode logReportNode = CommonReports.createDynawoLogReportNode(reportNode);
                new CsvLogParser().parse(logFile).forEach(e -> CommonReports.reportLogEntry(logReportNode, e));
            } else {
                LOGGER.warn("Dynawo logs file not found");
            }
        }

        private void updateNetwork(Path outputsFolder) {
            Path outputNetworkFile = outputsFolder.resolve(FINAL_STATE_FOLDER).resolve(OUTPUT_IIDM_FILENAME);
            if (Files.exists(outputNetworkFile)) {
                NetworkResultsUpdater.update(context.getNetwork(), NetworkSerDe.read(outputNetworkFile), context.getDynaWaltzParameters().isMergeLoads());
            } else {
                LOGGER.warn("Output IIDM file not found");
                status = DynamicSimulationResult.Status.FAILURE;
                statusText = "Dynawo Output IIDM file not found";
            }
        }

        private void setDumpFile(Path outputsFolder, Path dumpFileFolder, Path fileName) throws IOException {
            Path outputDumpFile = outputsFolder.resolve(FINAL_STATE_FOLDER).resolve(OUTPUT_DUMP_FILENAME);
            if (Files.exists(outputDumpFile)) {
                Files.copy(outputDumpFile, dumpFileFolder.resolve(fileName + "_" + OUTPUT_DUMP_FILENAME), StandardCopyOption.REPLACE_EXISTING);
            } else {
                LOGGER.warn("Dump file {} not found, export will be skipped", OUTPUT_DUMP_FILENAME);
            }
        }

        private void setTimeline(Path outputsFolder) {
            Path timelineFile = outputsFolder.resolve(DYNAWO_TIMELINE_FOLDER).resolve(TIMELINE_FILENAME);
            if (Files.exists(timelineFile)) {
                ReportNode timelineReportNode = DynawaltzReports.createDynaWaltzTimelineReportNode(reportNode);
                new CsvTimeLineParser().parse(timelineFile).forEach(e -> {
                    CommonReports.reportTimelineEntry(timelineReportNode, e);
                    timeline.add(new TimelineEvent(e.time(), e.modelName(), e.message()));
                });
            } else {
                LOGGER.warn("Timeline file not found");
            }
        }

        private void setCurves(Path workingDir) {
            Path curvesPath = workingDir.resolve(CURVES_OUTPUT_PATH).toAbsolutePath().resolve(CURVES_FILENAME);
            if (Files.exists(curvesPath)) {
                TimeSeries.parseCsv(curvesPath, new TimeSeriesCsvConfig(TimeSeriesConstants.DEFAULT_SEPARATOR, false, TimeFormat.FRACTIONS_OF_SECOND))
                        .values().forEach(l -> l.forEach(curve -> curves.put(curve.getMetadata().getName(), (DoubleTimeSeries) curve)));
            } else {
                LOGGER.warn("Curves folder not found");
                status = DynamicSimulationResult.Status.FAILURE;
                statusText = "Dynawo curves folder not found";
            }
        }

        private void writeInputFiles(Path workingDir) throws IOException {
            DynawoUtil.writeIidm(dynawoInput, workingDir.resolve(NETWORK_FILENAME));
            JobsXml.write(workingDir, context);
            DydXml.write(workingDir, context);
            ParametersXml.write(workingDir, context);
            if (context.withCurves()) {
                CurvesXml.write(workingDir, context);
            }
            DumpFileParameters dumpFileParameters = context.getDynaWaltzParameters().getDumpFileParameters();
            if (dumpFileParameters.useDumpFile()) {
                Path dumpFilePath = dumpFileParameters.getDumpFilePath();
                if (dumpFilePath != null) {
                    Files.copy(dumpFilePath, workingDir.resolve(dumpFileParameters.dumpFile()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
