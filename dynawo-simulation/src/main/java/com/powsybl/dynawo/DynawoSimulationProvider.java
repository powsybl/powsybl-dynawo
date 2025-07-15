/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import com.google.auto.service.AutoService;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.extensions.Extension;
import com.powsybl.commons.extensions.ExtensionJsonSerializer;
import com.powsybl.commons.parameters.Parameter;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.*;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.json.DynawoSimulationParametersSerializer;
import com.powsybl.dynawo.models.utils.BlackBoxSupplierUtils;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.iidm.network.Network;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynawo.DynawoSimulationConstants.JOBS_FILENAME;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicSimulationProvider.class)
public class DynawoSimulationProvider implements DynamicSimulationProvider {

    public static final String NAME = "Dynawo";
    private static final String WORKING_DIR_PREFIX = "dynawo_";

    private final DynawoSimulationConfig config;

    public DynawoSimulationProvider() {
        this(PlatformConfig.defaultConfig());
    }

    public DynawoSimulationProvider(PlatformConfig platformConfig) {
        this(DynawoSimulationConfig.load(platformConfig));
    }

    public DynawoSimulationProvider(DynawoSimulationConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getVersion() {
        return new PowsyblDynawoVersion().getMavenProjectVersion();
    }

    public static Command getCommand(DynawoSimulationConfig dynawoSimulationConfig) {
        return new GroupCommandBuilder()
                .id("dyn_fs")
                .subCommand()
                .program(dynawoSimulationConfig.getProgram())
                .args("jobs", JOBS_FILENAME)
                .add()
                .build();
    }

    public static Command getVersionCommand(DynawoSimulationConfig dynawoSimulationConfig) {
        List<String> args = Collections.singletonList("version");
        return new SimpleCommandBuilder()
                .id("dynawo_version")
                .program(dynawoSimulationConfig.getProgram())
                .args(args)
                .build();
    }

    @Override
    public CompletableFuture<DynamicSimulationResult> run(Network network, DynamicModelsSupplier dynamicModelsSupplier, EventModelsSupplier eventModelsSupplier, OutputVariablesSupplier outputVariablesSupplier, String workingVariantId,
                                                          ComputationManager computationManager, DynamicSimulationParameters parameters, ReportNode reportNode) {
        Objects.requireNonNull(dynamicModelsSupplier);
        Objects.requireNonNull(eventModelsSupplier);
        Objects.requireNonNull(outputVariablesSupplier);
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(reportNode);

        ReportNode dsReportNode = DynawoSimulationReports.createDynawoSimulationReportNode(reportNode, network.getId());
        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, config.isDebug(), parameters.getDebugDir());
        DynawoVersion currentVersion = DynawoUtil.requireDynaMinVersion(execEnv, computationManager, getVersionCommand(config), DynawoSimulationConfig.DYNAWO_LAUNCHER_PROGRAM_NAME, false);
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, BlackBoxSupplierUtils.getBlackBoxModelList(dynamicModelsSupplier, network, dsReportNode))
                .workingVariantId(workingVariantId)
                .dynamicSimulationParameters(parameters)
                .dynawoParameters(DynawoSimulationParameters.load(parameters))
                .eventModels(BlackBoxSupplierUtils.getBlackBoxModelList(eventModelsSupplier, network, dsReportNode))
                .outputVariables(outputVariablesSupplier.get(network))
                .currentVersion(currentVersion)
                .reportNode(reportNode)
                .build();

        return computationManager.execute(execEnv, new DynawoSimulationHandler(context, getCommand(config), reportNode));
    }

    @Override
    public Optional<Class<? extends Extension<DynamicSimulationParameters>>> getSpecificParametersClass() {
        return Optional.of(DynawoSimulationParameters.class);
    }

    @Override
    public Optional<ExtensionJsonSerializer> getSpecificParametersSerializer() {
        return Optional.of(new DynawoSimulationParametersSerializer());
    }

    @Override
    public Optional<Extension<DynamicSimulationParameters>> loadSpecificParameters(PlatformConfig platformConfig) {
        return Optional.of(DynawoSimulationParameters.load(platformConfig));
    }

    @Override
    public Optional<Extension<DynamicSimulationParameters>> loadSpecificParameters(Map<String, String> properties) {
        return Optional.of(DynawoSimulationParameters.load(properties));
    }

    @Override
    public Map<String, String> createMapFromSpecificParameters(Extension<DynamicSimulationParameters> extension) {
        if (extension instanceof DynawoSimulationParameters dsp) {
            return dsp.createMapFromParameters();
        }
        return Collections.emptyMap();
    }

    @Override
    public void updateSpecificParameters(Extension<DynamicSimulationParameters> extension, Map<String, String> properties) {
        if (extension instanceof DynawoSimulationParameters dsp) {
            dsp.update(properties);
        }
    }

    @Override
    public List<Parameter> getSpecificParameters() {
        return DynawoSimulationParameters.SPECIFIC_PARAMETERS;
    }

    @Override
    public Optional<ModuleConfig> getModuleConfig(PlatformConfig platformConfig) {
        return platformConfig.getOptionalModuleConfig(DynawoSimulationParameters.MODULE_SPECIFIC_PARAMETERS);
    }
}
