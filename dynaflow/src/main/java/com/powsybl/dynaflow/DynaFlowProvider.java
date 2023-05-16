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

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
@AutoService(LoadFlowProvider.class)
public class DynaFlowProvider implements LoadFlowProvider {

    public static final String MODULE_SPECIFIC_PARAMETERS = "dynaflow-default-parameters";

    private static final String WORKING_DIR_PREFIX = "dynaflow_";

    private final Supplier<DynaFlowConfig> configSupplier;

    public DynaFlowProvider() {
        this(DynaFlowConfig::fromPropertyFile);
    }

    public DynaFlowProvider(Supplier<DynaFlowConfig> configSupplier) {
        this.configSupplier = Suppliers.memoize(Objects.requireNonNull(configSupplier, "Config supplier is null"));
    }

    private static String getProgram(DynaFlowConfig config) {
        return config.getHomeDir().resolve("dynaflow-launcher.sh").toString();
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
    public CompletableFuture<LoadFlowResult> run(Network network, ComputationManager computationManager, String workingStateId, LoadFlowParameters loadFlowParameters) {
        Objects.requireNonNull(network);
        Objects.requireNonNull(computationManager);
        Objects.requireNonNull(workingStateId);
        Objects.requireNonNull(loadFlowParameters);

        DynaFlowParameters dynaFlowParameters = getParametersExt(loadFlowParameters);
        DynaFlowConfig config = Objects.requireNonNull(configSupplier.get());
        ExecutionEnvironment env = new ExecutionEnvironment(config.createEnv(), WORKING_DIR_PREFIX, config.isDebug());
        DynawoUtil.requireDynawoMinVersion(env, computationManager, getVersionCommand(config), true);

        return computationManager.execute(env, new DynaFlowHandler(network, workingStateId, dynaFlowParameters, loadFlowParameters, config));
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
