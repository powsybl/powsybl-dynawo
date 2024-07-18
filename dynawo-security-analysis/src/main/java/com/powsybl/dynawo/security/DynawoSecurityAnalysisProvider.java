/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security;

import com.google.auto.service.AutoService;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.Command;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.computation.SimpleCommandBuilder;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.models.utils.BlackBoxSupplierUtils;
import com.powsybl.dynawo.xml.DynawoSimulationConstants;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.SecurityAnalysisReport;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisProvider;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisRunParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynawo.DynawoSimulationConfig.DYNAWO_LAUNCHER_PROGRAM_NAME;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicSecurityAnalysisProvider.class)
public class DynawoSecurityAnalysisProvider implements DynamicSecurityAnalysisProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoSecurityAnalysisProvider.class);
    private static final String WORKING_DIR_PREFIX = "dynawo_sa_";
    private final DynawoAlgorithmsConfig config;

    public DynawoSecurityAnalysisProvider() {
        this(PlatformConfig.defaultConfig());
    }

    public DynawoSecurityAnalysisProvider(PlatformConfig platformConfig) {
        this(DynawoAlgorithmsConfig.load(platformConfig));
    }

    public DynawoSecurityAnalysisProvider(DynawoAlgorithmsConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public CompletableFuture<SecurityAnalysisReport> run(Network network, String workingVariantId,
                                                         DynamicModelsSupplier dynamicModelsSupplier,
                                                         ContingenciesProvider contingenciesProvider,
                                                         DynamicSecurityAnalysisRunParameters runParameters) {

        if (!runParameters.getMonitors().isEmpty()) {
            LOGGER.error("Monitoring is not possible with Dynawo implementation. There will not be supplementary information about monitored equipment.");
        }
        if (!runParameters.getOperatorStrategies().isEmpty()) {
            LOGGER.error("Strategies are not implemented in Dynawo");
        }
        if (!runParameters.getActions().isEmpty()) {
            LOGGER.error("Actions are not implemented in Dynawo");
        }

        ReportNode dsaReportNode = DynamicSecurityAnalysisReports.createDynamicSecurityAnalysisReportNode(runParameters.getReportNode(), network.getId());
        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment execEnv = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, config.isDebug());
        DynawoUtil.requireDynaMinVersion(execEnv, runParameters.getComputationManager(), getVersionCommand(config), DYNAWO_LAUNCHER_PROGRAM_NAME, false);
        List<Contingency> contingencies = contingenciesProvider.getContingencies(network);
        DynamicSecurityAnalysisParameters parameters = runParameters.getDynamicSecurityAnalysisParameters();
        SecurityAnalysisContext context = new SecurityAnalysisContext(network, workingVariantId,
                BlackBoxSupplierUtils.getBlackBoxModelList(dynamicModelsSupplier, network, dsaReportNode),
                parameters,
                DynawoSimulationParameters.load(parameters.getDynamicSimulationParameters()),
                contingencies);

        return runParameters.getComputationManager().execute(execEnv, new DynawoSecurityAnalysisHandler(context, getCommand(config), runParameters.getFilter(), runParameters.getInterceptors(), dsaReportNode));
    }

    @Override
    public String getName() {
        return DynawoSimulationProvider.NAME;
    }

    @Override
    public String getVersion() {
        return new PowsyblDynawoVersion().getMavenProjectVersion();
    }

    public static Command getCommand(DynawoAlgorithmsConfig config) {
        List<String> args = Arrays.asList(
                "SA",
                "--input", DynawoSimulationConstants.MULTIPLE_JOBS_FILENAME,
                "--output", DynawoSimulationConstants.AGGREGATED_RESULTS);
        return new SimpleCommandBuilder()
                .id("dynawo_dynamic_sa")
                .program(config.getProgram())
                .args(args)
                .build();
    }

    public static Command getVersionCommand(DynawoAlgorithmsConfig config) {
        List<String> args = Collections.singletonList("--version");
        return new SimpleCommandBuilder()
                .id("dynawo_version")
                .program(config.getProgram())
                .args(args)
                .build();
    }
}
