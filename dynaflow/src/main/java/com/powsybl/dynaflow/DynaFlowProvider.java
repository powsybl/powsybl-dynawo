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
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.Extension;
import com.powsybl.commons.extensions.ExtensionJsonSerializer;
import com.powsybl.commons.parameters.Parameter;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.*;
import com.powsybl.dynaflow.json.JsonDynaFlowParametersSerializer;
import com.powsybl.dynawo.commons.*;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowProvider;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.LoadFlowRunParameters;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static com.powsybl.dynaflow.DynaFlowParameters.MODULE_SPECIFIC_PARAMETERS;
import static com.powsybl.dynawo.commons.DynawoConstants.NETWORK_FILENAME;
import static com.powsybl.dynawo.commons.DynawoConstants.OUTPUT_IIDM_FILENAME_PATH;

/**
 *
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(LoadFlowProvider.class)
public class DynaFlowProvider implements LoadFlowProvider {

    private static final String WORKING_DIR_PREFIX = "dynaflow_";

    private final Supplier<DynaFlowConfig> configSupplier;

    public DynaFlowProvider() {
        this(DynaFlowConfig::load);
    }

    public DynaFlowProvider(Supplier<DynaFlowConfig> configSupplier) {
        this.configSupplier = Suppliers.memoize(Objects.requireNonNull(configSupplier, "Config supplier is null"));
    }

    public static Command getCommand(DynaFlowConfig config) {
        return new SimpleCommandBuilder()
                .id("dynaflow_lf")
                .program(config.getProgram())
                .args("--network", NETWORK_FILENAME, "--config", CONFIG_FILENAME)
                .inputFiles(new InputFile(NETWORK_FILENAME),
                            new InputFile(CONFIG_FILENAME))
                .outputFiles(new OutputFile(OUTPUT_RESULTS_FILENAME),
                             new OutputFile(OUTPUT_IIDM_FILENAME_PATH))
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

    static DynaFlowParameters getParametersExt(LoadFlowParameters parameters) {
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        if (parametersExt == null) {
            return new DynaFlowParameters();
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
    public CompletableFuture<LoadFlowResult> run(Network network, String workingStateId, LoadFlowRunParameters loadFlowRunParameters) {
        Objects.requireNonNull(network);
        Objects.requireNonNull(workingStateId);
        Objects.requireNonNull(loadFlowRunParameters);
        ComputationManager computationManager = Objects.requireNonNull(loadFlowRunParameters.getComputationManager());
        LoadFlowParameters loadFlowParameters = Objects.requireNonNull(loadFlowRunParameters.getLoadFlowParameters());
        ReportNode reportNode = Objects.requireNonNull(loadFlowRunParameters.getReportNode());

        DynaFlowParameters dynaFlowParameters = getParametersExt(loadFlowParameters);
        DynaFlowParameters.log(loadFlowParameters, dynaFlowParameters);
        DynaFlowConfig config = Objects.requireNonNull(configSupplier.get());

        ExecutionEnvironment execEnvVersionCheck = ExecutionEnvironmentUtils.createVersionEnv(config, WORKING_DIR_PREFIX);
        Command versionCmd = getVersionCommand(config);
        DynawoVersion version = DynawoUtil.requireDynaMinVersion(execEnvVersionCheck, computationManager, versionCmd, DynaFlowConfig.DYNAFLOW_LAUNCHER_PROGRAM_NAME, true);

        ExecutionEnvironment execEnvSimulation = ExecutionEnvironmentUtils.createSimulationEnv(config, WORKING_DIR_PREFIX);
        return computationManager.execute(execEnvSimulation, new DynaFlowHandler(network, workingStateId, dynaFlowParameters, loadFlowParameters, getCommand(config), version, reportNode));
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
    public void updateSpecificParameters(Extension<LoadFlowParameters> extension, PlatformConfig platformConfig) {
        ((DynaFlowParameters) extension).update(platformConfig);
    }

    @Override
    public Optional<Extension<LoadFlowParameters>> loadSpecificParameters(Map<String, String> properties) {
        return Optional.of(DynaFlowParameters.load(properties));
    }

    @Override
    public void updateSpecificParameters(Extension<LoadFlowParameters> extension, Map<String, String> properties) {
        getParametersExt(extension.getExtendable()).update(properties);
    }

    @Override
    public Map<String, String> createMapFromSpecificParameters(Extension<LoadFlowParameters> extension) {
        if (extension instanceof DynaFlowParameters dfp) {
            return dfp.createMapFromParameters();
        }
        return Collections.emptyMap();
    }

    @Override
    public List<Parameter> getRawSpecificParameters() {
        return DynaFlowParameters.SPECIFIC_PARAMETERS;
    }

    @Override
    public Optional<ModuleConfig> getModuleConfig(PlatformConfig platformConfig) {
        return platformConfig.getOptionalModuleConfig(MODULE_SPECIFIC_PARAMETERS);
    }

    @Override
    public Optional<ExtensionJsonSerializer> getSpecificParametersSerializer() {
        return Optional.of(new JsonDynaFlowParametersSerializer());
    }
}
