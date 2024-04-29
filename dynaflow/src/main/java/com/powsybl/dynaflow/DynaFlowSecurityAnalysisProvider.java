/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.auto.service.AutoService;
import com.powsybl.action.Action;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.Command;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.computation.SimpleCommandBuilder;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.*;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;
import com.powsybl.security.limitreduction.LimitReduction;
import com.powsybl.security.monitor.StateMonitor;
import com.powsybl.security.strategy.OperatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static com.powsybl.dynaflow.SecurityAnalysisConstants.CONTINGENCIES_FILENAME;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
@AutoService(SecurityAnalysisProvider.class)
public class DynaFlowSecurityAnalysisProvider implements SecurityAnalysisProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DynaFlowSecurityAnalysisProvider.class);
    private static final String WORKING_DIR_PREFIX = "dynaflow_sa_";
    private final Supplier<DynaFlowConfig> configSupplier;

    public DynaFlowSecurityAnalysisProvider() {
        this(DynaFlowConfig::load);
    }

    public DynaFlowSecurityAnalysisProvider(Supplier<DynaFlowConfig> configSupplier) {
        this.configSupplier = Objects.requireNonNull(configSupplier);
    }

    @Override
    public CompletableFuture<SecurityAnalysisReport> run(Network network,
                                                         String workingVariantId,
                                                         LimitViolationDetector detector,
                                                         LimitViolationFilter filter,
                                                         ComputationManager computationManager,
                                                         SecurityAnalysisParameters parameters,
                                                         ContingenciesProvider contingenciesProvider,
                                                         List<SecurityAnalysisInterceptor> interceptors,
                                                         List<OperatorStrategy> operatorStrategies,
                                                         List<Action> actions,
                                                         List<StateMonitor> monitors,
                                                         List<LimitReduction> limitReductions,
                                                         ReportNode reportNode) {
        if (detector != null) {
            LOG.error("LimitViolationDetector is not used in Dynaflow implementation.");
        }
        if (monitors != null && !monitors.isEmpty()) {
            LOG.error("Monitoring is not possible with Dynaflow implementation. There will not be supplementary information about monitored equipment.");
        }
        if (operatorStrategies != null && !operatorStrategies.isEmpty()) {
            LOG.error("Strategies are not implemented in Dynaflow");
        }
        if (actions != null && !actions.isEmpty()) {
            LOG.error("Actions are not implemented in Dynaflow");
        }
        if (limitReductions != null && !limitReductions.isEmpty()) {
            LOG.error("Limit reductions are not implemented in Dynaflow");
        }

        Objects.requireNonNull(computationManager);
        Objects.requireNonNull(network);
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(filter);
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(contingenciesProvider);
        interceptors.forEach(Objects::requireNonNull);

        DynaFlowConfig config = Objects.requireNonNull(configSupplier.get());
        ExecutionEnvironment execEnv = new ExecutionEnvironment(config.createEnv(), WORKING_DIR_PREFIX, config.isDebug());
        DynawoUtil.requireDynaMinVersion(execEnv, computationManager, getVersionCommand(config), DynaFlowConfig.DYNAFLOW_LAUNCHER_PROGRAM_NAME, true);
        List<Contingency> contingencies = contingenciesProvider.getContingencies(network);
        ReportNode dfsaReportNode = Reports.createDynaFlowSecurityAnalysisReportNode(reportNode, network.getId());

        DynaFlowSecurityAnalysisHandler executionHandler = new DynaFlowSecurityAnalysisHandler(network, workingVariantId, getCommand(config), parameters, contingencies, filter, interceptors, dfsaReportNode);
        return computationManager.execute(execEnv, executionHandler);
    }

    @Override
    public String getName() {
        return DYNAFLOW_NAME;
    }

    @Override
    public String getVersion() {
        return new PowsyblDynawoVersion().getMavenProjectVersion();
    }

    public static Command getCommand(DynaFlowConfig config) {
        List<String> args = Arrays.asList("--network", IIDM_FILENAME,
                "--config", CONFIG_FILENAME,
                "--contingencies", CONTINGENCIES_FILENAME);
        return new SimpleCommandBuilder()
                .id("dynaflow_sa")
                .program(config.getProgram())
                .args(args)
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
}
