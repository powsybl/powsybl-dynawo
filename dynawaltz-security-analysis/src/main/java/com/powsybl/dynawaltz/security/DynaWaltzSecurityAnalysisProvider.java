/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.security;

import com.google.auto.service.AutoService;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.computation.Command;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.computation.SimpleCommandBuilder;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.models.utils.BlackBoxSupplierUtils;
import com.powsybl.dynawaltz.xml.DynaWaltzConstants;
import com.powsybl.dynawo.commons.DynawoConfigFactory;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.dynawo.commons.SimpleDynawoConfig;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.LimitViolationDetector;
import com.powsybl.security.LimitViolationFilter;
import com.powsybl.security.SecurityAnalysisReport;
import com.powsybl.security.action.Action;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisProvider;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;
import com.powsybl.security.monitor.StateMonitor;
import com.powsybl.security.strategy.OperatorStrategy;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicSecurityAnalysisProvider.class)
public class DynaWaltzSecurityAnalysisProvider implements DynamicSecurityAnalysisProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynaWaltzSecurityAnalysisProvider.class);
    private static final String WORKING_DIR_PREFIX = "dynawaltz_sa_";
    private static final String MODULE_NAME = "dynawo-algorithms";
    private static final String LAUNCHER_PROGRAM_NAME = "dynawo-algorithms";

    private final SimpleDynawoConfig dynawoConfig;

    public DynaWaltzSecurityAnalysisProvider() {
        this(PlatformConfig.defaultConfig());
    }

    public DynaWaltzSecurityAnalysisProvider(PlatformConfig platformConfig) {
        this(DynawoConfigFactory.load(platformConfig, MODULE_NAME));
    }

    public DynaWaltzSecurityAnalysisProvider(SimpleDynawoConfig dynawoConfig) {
        this.dynawoConfig = Objects.requireNonNull(dynawoConfig);
    }

    @Override
    public CompletableFuture<SecurityAnalysisReport> run(Network network,
                                                         DynamicModelsSupplier dynamicModelsSupplier,
                                                         EventModelsSupplier eventModelsSupplier,
                                                         String workingVariantId,
                                                         LimitViolationDetector detector,
                                                         LimitViolationFilter filter,
                                                         ComputationManager computationManager,
                                                         DynamicSecurityAnalysisParameters parameters,
                                                         ContingenciesProvider contingenciesProvider,
                                                         List<SecurityAnalysisInterceptor> interceptors,
                                                         List<OperatorStrategy> operatorStrategies,
                                                         List<Action> actions,
                                                         List<StateMonitor> monitors,
                                                         Reporter reporter) {
        if (detector != null) {
            LOGGER.error("LimitViolationDetector is not used in Dynaflow implementation.");
        }
        if (monitors != null && !monitors.isEmpty()) {
            LOGGER.error("Monitoring is not possible with Dynaflow implementation. There will not be supplementary information about monitored equipment.");
        }
        if (reporter != Reporter.NO_OP) {
            LOGGER.warn("Reporters are not used in Dynaflow implementation");
        }
        if (operatorStrategies != null && !operatorStrategies.isEmpty()) {
            LOGGER.error("Strategies are not implemented in Dynaflow");
        }
        if (actions != null && !actions.isEmpty()) {
            LOGGER.error("Actions are not implemented in Dynaflow");
        }

        Objects.requireNonNull(computationManager);
        Objects.requireNonNull(network);
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(dynamicModelsSupplier);
        Objects.requireNonNull(eventModelsSupplier);
        Objects.requireNonNull(filter);
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(contingenciesProvider);
        interceptors.forEach(Objects::requireNonNull);

        Reporter dsaReporter = Reports.createDynamicSecurityAnalysisReporter(reporter, network.getId());
        network.getVariantManager().setWorkingVariant(workingVariantId);
        ExecutionEnvironment env = new ExecutionEnvironment(dynawoConfig.createEnv(), WORKING_DIR_PREFIX, dynawoConfig.isDebug());
        DynawoUtil.requireDynaMinVersion(env, computationManager, getVersionCommand(dynawoConfig), MODULE_NAME, false);
        List<Contingency> contingencies = contingenciesProvider.getContingencies(network);
        SecurityAnalysisContext context = new SecurityAnalysisContext(network, workingVariantId,
                BlackBoxSupplierUtils.getBlackBoxModelList(dynamicModelsSupplier, network, dsaReporter),
                BlackBoxSupplierUtils.getBlackBoxModelList(eventModelsSupplier, network, dsaReporter),
                parameters,
                //TODO fix
                DynaWaltzParameters.load(),
                contingencies);

        return computationManager.execute(env, new DynaWaltzSecurityAnalysisHandler(context, dynawoConfig, filter, interceptors));
    }

    // TODO choose another name ? (needed for models supplier)
    @Override
    public String getName() {
        return DynaWaltzProvider.NAME;
    }

    @Override
    public String getVersion() {
        return new PowsyblDynawoVersion().getMavenProjectVersion();
    }

    public static Command getCommand(SimpleDynawoConfig config) {
        List<String> args = Arrays.asList(
                "SA",
                "--input", DynaWaltzConstants.MULTIPLE_JOBS_FILENAME,
                "--output", DynaWaltzConstants.AGGREGATED_RESULTS);
        return new SimpleCommandBuilder()
                .id("dynawaltz_sa")
                .program(getProgram(config))
                .args(args)
                .build();
    }

    public static Command getVersionCommand(SimpleDynawoConfig config) {
        List<String> args = Collections.singletonList("--version");
        return new SimpleCommandBuilder()
                .id("dynawo_algorithms_version")
                .program(getProgram(config))
                .args(args)
                .build();
    }

    private static String getProgram(SimpleDynawoConfig config) {
        String extension = SystemUtils.IS_OS_WINDOWS ? ".cmd" : ".sh";
        return config.getHomeDir().resolve(LAUNCHER_PROGRAM_NAME + extension).toString();
    }
}
