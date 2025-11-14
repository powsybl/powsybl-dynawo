/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation;

import com.powsybl.commons.Versionable;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.builders.AdditionalModelConfigLoader;
import com.powsybl.dynawo.builders.ModelConfigsHandler;
import com.powsybl.dynawo.algorithms.DynawoAlgorithmsConfig;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultsProvider;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResults;
import com.powsybl.dynawo.models.utils.BlackBoxSupplierUtils;
import com.powsybl.iidm.network.Network;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynawo.DynawoSimulationConfig.DYNAWO_LAUNCHER_PROGRAM_NAME;
import static com.powsybl.dynawo.algorithms.DynawoAlgorithmsCommandUtil.getCommand;
import static com.powsybl.dynawo.algorithms.DynawoAlgorithmsCommandUtil.getVersionCommand;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationProvider implements Versionable {
    private static final String WORKING_DIR_PREFIX = "dynawo_ctc_";
    private final DynawoAlgorithmsConfig config;

    public CriticalTimeCalculationProvider() {
        this(PlatformConfig.defaultConfig());
    }

    public CriticalTimeCalculationProvider(PlatformConfig platformConfig) {
        this(DynawoAlgorithmsConfig.load(platformConfig));
    }

    public CriticalTimeCalculationProvider(DynawoAlgorithmsConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    public CompletableFuture<CriticalTimeCalculationResults> run(Network network, String workingVariantId,
                                                                 DynamicModelsSupplier dynamicModelsSupplier,
                                                                 NodeFaultsProvider nodeFaultsProvider,
                                                                 CriticalTimeCalculationRunParameters runParameters) {

        ReportNode mcReportNode = CriticalTimeCalculationReports.createCriticalTimeCalculationReportNode(runParameters.getReportNode(), network.getId());
        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, config.isDebug(), runParameters.getCriticalTimeCalculationParameters().getDebugDir());
        DynawoVersion currentVersion = DynawoUtil.requireDynaMinVersion(execEnv, runParameters.getComputationManager(), getVersionCommand(config), DYNAWO_LAUNCHER_PROGRAM_NAME, false);
        CriticalTimeCalculationParameters parameters = runParameters.getCriticalTimeCalculationParameters();
        DynawoSimulationParameters dynawoParameters = parameters.getDynawoParameters();
        dynawoParameters.getAdditionalModelsPath().ifPresent(additionalModelPath ->
                ModelConfigsHandler.getInstance().addModels(new AdditionalModelConfigLoader(additionalModelPath)));
        CriticalTimeCalculationContext context = new CriticalTimeCalculationContext.Builder(network,
                BlackBoxSupplierUtils.getBlackBoxModelList(dynamicModelsSupplier, network, mcReportNode),
                nodeFaultsProvider.getNodeFaults(network, mcReportNode))
                .criticalTimeCalculationParameters(parameters)
                .dynawoParameters(dynawoParameters)
                .currentVersion(currentVersion)
                .reportNode(mcReportNode)
                .workingVariantId(workingVariantId)
                .build();
        return runParameters.getComputationManager().execute(execEnv,
                new CriticalTimeCalculationHandler(context, getCommand(config, "CTC", "dynawo_dynamic_ctc"), mcReportNode));
    }

    @Override
    public String getName() {
        return DynawoSimulationProvider.NAME;
    }

    @Override
    public String getVersion() {
        return new PowsyblDynawoVersion().getMavenProjectVersion();
    }
}
