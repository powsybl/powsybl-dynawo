/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.commons.Versionable;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.dynawo.margincalculation.loadsvariation.supplier.LoadsVariationSupplier;
import com.powsybl.dynawo.margincalculation.results.MarginCalculationResult;
import com.powsybl.dynawo.models.utils.BlackBoxSupplierUtils;
import com.powsybl.dynawo.algorithms.DynawoAlgorithmsConfig;
import com.powsybl.iidm.network.Network;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynawo.DynawoSimulationConfig.DYNAWO_LAUNCHER_PROGRAM_NAME;
import static com.powsybl.dynawo.algorithms.DynawoAlgorithmsCommandUtil.getCommand;
import static com.powsybl.dynawo.algorithms.DynawoAlgorithmsCommandUtil.getVersionCommand;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
//TODO add an interface and API
public class MarginCalculationProvider implements Versionable {

    private static final String WORKING_DIR_PREFIX = "dynawo_mc_";
    private final DynawoAlgorithmsConfig config;

    public MarginCalculationProvider() {
        this(PlatformConfig.defaultConfig());
    }

    public MarginCalculationProvider(PlatformConfig platformConfig) {
        this(DynawoAlgorithmsConfig.load(platformConfig));
    }

    public MarginCalculationProvider(DynawoAlgorithmsConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    public CompletableFuture<MarginCalculationResult> run(Network network, String workingVariantId,
                                                          DynamicModelsSupplier dynamicModelsSupplier,
                                                          ContingenciesProvider contingenciesProvider,
                                                          LoadsVariationSupplier loadsVariationSupplier,
                                                          MarginCalculationRunParameters runParameters) {

        ReportNode mcReportNode = MarginCalculationReports.createMarginCalculationReportNode(runParameters.getReportNode(), network.getId());
        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, config.isDebug());
        DynawoUtil.requireDynaMinVersion(execEnv, runParameters.getComputationManager(), getVersionCommand(config), DYNAWO_LAUNCHER_PROGRAM_NAME, false);
        MarginCalculationParameters parameters = runParameters.getMarginCalculationParameters();
        MarginCalculationContext context = new MarginCalculationContext(network, workingVariantId,
                BlackBoxSupplierUtils.getBlackBoxModelList(dynamicModelsSupplier, network, mcReportNode),
                parameters,
                //TODO fix
                DynawoSimulationParameters.load(),
                contingenciesProvider.getContingencies(network),
                loadsVariationSupplier.getLoadsVariations(network));

        return runParameters.getComputationManager().execute(execEnv,
                new MarginCalculationHandler(context, getCommand(config, "MC", "dynawo_dynamic_mc"), mcReportNode));
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
