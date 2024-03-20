/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.auto.service.AutoService;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.Extension;
import com.powsybl.commons.extensions.ExtensionJsonSerializer;
import com.powsybl.commons.parameters.Parameter;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.*;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.dynaflow.json.JsonDynaFlowParametersSerializer;
import com.powsybl.dynawo.commons.CommonReports;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.NetworkResultsUpdater;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.dynawo.commons.timeline.TimelineEntry;
import com.powsybl.dynawo.commons.timeline.XmlTimeLineParser;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.serde.NetworkSerDe;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowProvider;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.LoadFlowResultImpl;
import com.powsybl.loadflow.json.LoadFlowResultDeserializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static com.powsybl.dynaflow.DynaFlowParameters.*;
import static com.powsybl.dynawo.commons.DynawoConstants.DYNAWO_TIMELINE_FOLDER;

/**
 *
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 */
@AutoService(LoadFlowProvider.class)
public class DynaFlowProvider implements LoadFlowProvider {

    public static final String MODULE_SPECIFIC_PARAMETERS = "dynaflow-default-parameters";

    private static final String WORKING_DIR_PREFIX = "dynaflow_";

    private final Supplier<DynaFlowConfig> configSupplier;

    public DynaFlowProvider() {
        this(DynaFlowConfig::load);
    }

    public DynaFlowProvider(Supplier<DynaFlowConfig> configSupplier) {
        this.configSupplier = Suppliers.memoize(Objects.requireNonNull(configSupplier, "Config supplier is null"));
    }

    private static String getProgram(DynaFlowConfig config) {
        return config.getProgram(DynaFlowConstants.DYNAFLOW_LAUNCHER_PROGRAM_NAME);
    }

    public static Command getCommand(DynaFlowConfig config) {
        List<String> args = Arrays.asList("--network", IIDM_FILENAME, "--config", CONFIG_FILENAME);

        return new SimpleCommandBuilder()
                .id("dynaflow_lf")
                .program(getProgram(config))
                .args(args)
                .inputFiles(new InputFile(IIDM_FILENAME),
                            new InputFile(CONFIG_FILENAME))
                .outputFiles(new OutputFile(OUTPUT_RESULTS_FILENAME),
                             new OutputFile("outputs/finalState/" + OUTPUT_IIDM_FILENAME))
                .build();
    }

    public static Command getVersionCommand(DynaFlowConfig config) {
        List<String> args = Collections.singletonList("--version");
        return new SimpleCommandBuilder()
                .id("dynaflow_version")
                .program(getProgram(config))
                .args(args)
                .build();
    }

    private static DynaFlowParameters getParametersExt(LoadFlowParameters parameters) {
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        if (parametersExt == null) {
            parametersExt = new DynaFlowParameters();
        }
        return parametersExt;
    }

    @Override
    public String getName() {
        return DYNAFLOW_NAME;
    }

    @Override
    public String getVersion() {
        return new PowsyblDynawoVersion().getMavenProjectVersion();
    }

    @Override
    public CompletableFuture<LoadFlowResult> run(Network network, ComputationManager computationManager, String workingStateId,
                                                 LoadFlowParameters loadFlowParameters, ReportNode reportNode) {
        Objects.requireNonNull(network);
        Objects.requireNonNull(computationManager);
        Objects.requireNonNull(workingStateId);
        Objects.requireNonNull(loadFlowParameters);
        DynaFlowParameters dynaFlowParameters = getParametersExt(loadFlowParameters);
        DynaFlowConfig config = Objects.requireNonNull(configSupplier.get());
        ExecutionEnvironment env = new ExecutionEnvironment(config.createEnv(), WORKING_DIR_PREFIX, config.isDebug());
        Command versionCmd = getVersionCommand(config);
        DynawoUtil.requireDynaMinVersion(env, computationManager, versionCmd, DYNAFLOW_LAUNCHER_PROGRAM_NAME, true);
        return computationManager.execute(env, new DynaFlowHandler(network, workingStateId, dynaFlowParameters, loadFlowParameters, config, reportNode));
    }

    @Override
    public Optional<Class<? extends Extension<LoadFlowParameters>>> getSpecificParametersClass() {
        return Optional.of(DynaFlowParameters.class);
    }

    @Override
    public Optional<Extension<LoadFlowParameters>> loadSpecificParameters(PlatformConfig platformConfig) {
        // if not specified, dynaflow parameters must be default here
        return Optional.of(DynaFlowParameters.load(platformConfig));
    }

    @Override
    public Optional<Extension<LoadFlowParameters>> loadSpecificParameters(Map<String, String> properties) {
        return Optional.of(DynaFlowParameters.load(properties));
    }

    @Override
    public Map<String, String> createMapFromSpecificParameters(Extension<LoadFlowParameters> extension) {
        return Map.ofEntries(
                Map.entry(SVC_REGULATION_ON, Boolean.toString(((DynaFlowParameters) extension).getSvcRegulationOn())),
                Map.entry(SHUNT_REGULATION_ON, Boolean.toString(((DynaFlowParameters) extension).getShuntRegulationOn())),
                Map.entry(AUTOMATIC_SLACK_BUS_ON, Boolean.toString(((DynaFlowParameters) extension).getAutomaticSlackBusOn())),
                Map.entry(DSO_VOLTAGE_LEVEL, Double.toString(((DynaFlowParameters) extension).getDsoVoltageLevel())),
                Map.entry(ACTIVE_POWER_COMPENSATION, ((DynaFlowParameters) extension).getActivePowerCompensation().name()),
                Map.entry(SETTING_PATH, ((DynaFlowParameters) extension).getSettingPath()),
                Map.entry(ASSEMBLING_PATH, ((DynaFlowParameters) extension).getAssemblingPath()),
                Map.entry(START_TIME, Double.toString(((DynaFlowParameters) extension).getStartTime())),
                Map.entry(STOP_TIME, Double.toString(((DynaFlowParameters) extension).getStopTime())),
                Map.entry(PRECISION_NAME, Double.toString(((DynaFlowParameters) extension).getPrecision())),
                Map.entry(Sa.TIME_OF_EVENT, Double.toString(((DynaFlowParameters) extension).getSa().getTimeOfEvent())),
                Map.entry(CHOSEN_OUTPUTS, String.join(", ", ((DynaFlowParameters) extension).getChosenOutputs())),
                Map.entry(TIME_STEP, Double.toString(((DynaFlowParameters) extension).getTimeStep())),
                Map.entry(STARTING_POINT_MODE, ((DynaFlowParameters) extension).getStartingPointMode().name()),
                Map.entry(MERGE_LOADS, Boolean.toString(((DynaFlowParameters) extension).isMergeLoads())));
    }

    @Override
    public List<Parameter> getSpecificParameters() {
        return DynaFlowParameters.SPECIFIC_PARAMETERS;
    }

    @Override
    public Optional<ExtensionJsonSerializer> getSpecificParametersSerializer() {
        return Optional.of(new JsonDynaFlowParametersSerializer());
    }

    @Override
    public void updateSpecificParameters(Extension<LoadFlowParameters> extension, Map<String, String> properties) {
        getParametersExt(extension.getExtendable()).update(properties);
    }

    private static class DynaFlowHandler extends AbstractExecutionHandler<LoadFlowResult> {
        private final Network network;
        private final Network dynawoInput;
        private final String workingStateId;
        private final DynaFlowParameters dynaFlowParameters;
        private final LoadFlowParameters loadFlowParameters;
        private final DynaFlowConfig config;
        private final ReportNode reportNode;

        public DynaFlowHandler(Network network, String workingStateId, DynaFlowParameters dynaFlowParameters, LoadFlowParameters loadFlowParameters, DynaFlowConfig config, ReportNode reportNode) {
            this.network = network;
            this.workingStateId = workingStateId;
            this.dynaFlowParameters = dynaFlowParameters;
            this.loadFlowParameters = loadFlowParameters;
            this.config = config;
            this.dynawoInput = this.dynaFlowParameters.isMergeLoads() ? LoadsMerger.mergeLoads(this.network) : this.network;
            this.reportNode = reportNode;
        }

        @Override
        public List<CommandExecution> before(Path workingDir) throws IOException {
            network.getVariantManager().setWorkingVariant(workingStateId);
            DynawoUtil.writeIidm(dynawoInput, workingDir.resolve(IIDM_FILENAME));
            DynaFlowConfigSerializer.serialize(loadFlowParameters, dynaFlowParameters, Path.of("."), workingDir.resolve(CONFIG_FILENAME));
            return Collections.singletonList(createCommandExecution(config));
        }

        private static CommandExecution createCommandExecution(DynaFlowConfig config) {
            Command cmd = getCommand(config);
            return new CommandExecution(cmd, 1, 0);
        }

        @Override
        public LoadFlowResult after(Path workingDir, ExecutionReport report) {
            reportTimeLine(workingDir);

            report.log();
            network.getVariantManager().setWorkingVariant(workingStateId);
            boolean status = true;
            Path outputNetworkFile = workingDir.resolve("outputs").resolve("finalState").resolve(OUTPUT_IIDM_FILENAME);
            if (Files.exists(outputNetworkFile)) {
                NetworkResultsUpdater.update(network, NetworkSerDe.read(outputNetworkFile), dynaFlowParameters.isMergeLoads());
            } else {
                status = false;
            }
            Path resultsPath = workingDir.resolve(OUTPUT_RESULTS_FILENAME);
            if (!Files.exists(resultsPath)) {
                Map<String, String> metrics = new HashMap<>();
                List<LoadFlowResult.ComponentResult> componentResults = new ArrayList<>(1);
                componentResults.add(new LoadFlowResultImpl.ComponentResultImpl(0,
                        0,
                        status ? LoadFlowResult.ComponentResult.Status.CONVERGED : LoadFlowResult.ComponentResult.Status.FAILED,
                        0,
                        "not-found",
                        0.,
                        Double.NaN));
                return new LoadFlowResultImpl(status, metrics, null, componentResults);
            }
            return LoadFlowResultDeserializer.read(resultsPath);
        }

        private void reportTimeLine(Path workingDir) {
            ReportNode dfReportNode = DynaflowReports.createDynaFlowReportNode(reportNode, network.getId());
            Path timelineFile = workingDir.resolve(DYNAFLOW_OUTPUTS_FOLDER)
                    .resolve(DYNAWO_TIMELINE_FOLDER)
                    .resolve(DYNAFLOW_TIMELINE_FILE);
            List<TimelineEntry> tl = new XmlTimeLineParser().parse(timelineFile);
            tl.forEach(e -> CommonReports.reportTimelineEntry(dfReportNode, e));
        }
    }
}
