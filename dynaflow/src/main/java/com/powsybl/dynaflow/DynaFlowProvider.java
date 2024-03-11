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
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.computation.*;
import com.powsybl.dynaflow.json.JsonDynaFlowParametersSerializer;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowProvider;
import com.powsybl.loadflow.LoadFlowResult;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static com.powsybl.dynaflow.DynaFlowParameters.*;

/**
 *
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
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

    public static Command getCommand(DynaFlowConfig config) {
        List<String> args = Arrays.asList("--network", IIDM_FILENAME, "--config", CONFIG_FILENAME);

        return new SimpleCommandBuilder()
                .id("dynaflow_lf")
                .program(config.getProgram())
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
                .program(config.getProgram())
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
                                                 LoadFlowParameters loadFlowParameters, Reporter reporter) {
        Objects.requireNonNull(network);
        Objects.requireNonNull(computationManager);
        Objects.requireNonNull(workingStateId);
        Objects.requireNonNull(loadFlowParameters);

        DynaFlowParameters dynaFlowParameters = getParametersExt(loadFlowParameters);
        DynaFlowConfig config = Objects.requireNonNull(configSupplier.get());
        ExecutionEnvironment execEnv = new ExecutionEnvironment(config.createEnv(), WORKING_DIR_PREFIX, config.isDebug());
        Command versionCmd = getVersionCommand(config);
        DynawoUtil.requireDynaMinVersion(execEnv, computationManager, versionCmd, DynaFlowConfig.DYNAFLOW_LAUNCHER_PROGRAM_NAME, true);
        return computationManager.execute(execEnv, new DynaFlowHandler(network, workingStateId, dynaFlowParameters, loadFlowParameters, getCommand(config), reporter));
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
}
