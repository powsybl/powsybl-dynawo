/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.auto.service.AutoService;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.Command;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.computation.SimpleCommandBuilder;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoVersion;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.SecurityAnalysisProvider;
import com.powsybl.security.SecurityAnalysisReport;
import com.powsybl.security.SecurityAnalysisRunParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static com.powsybl.dynaflow.SecurityAnalysisConstants.CONTINGENCIES_FILENAME;
import static com.powsybl.dynaflow.DynaFlowConstants.DYNAFLOW_NAME;
import static com.powsybl.dynawo.commons.DynawoConstants.NETWORK_FILENAME;

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
                                                         ContingenciesProvider contingenciesProvider,
                                                         SecurityAnalysisRunParameters runParameters) {
        if (!runParameters.getMonitors().isEmpty()) {
            LOG.error("Monitoring is not possible with Dynaflow implementation. There will not be supplementary information about monitored equipment.");
        }
        if (!runParameters.getOperatorStrategies().isEmpty()) {
            LOG.error("Strategies are not implemented in Dynaflow");
        }
        if (!runParameters.getActions().isEmpty()) {
            LOG.error("Actions are not implemented in Dynaflow");
        }
        if (!runParameters.getLimitReductions().isEmpty()) {
            LOG.error("Limit reductions are not implemented in Dynaflow");
        }

        DynaFlowConfig config = Objects.requireNonNull(configSupplier.get());
        ExecutionEnvironment execEnv = new ExecutionEnvironment(config.createEnv(), WORKING_DIR_PREFIX, config.isDebug());
        DynawoUtil.requireDynaMinVersion(execEnv, runParameters.getComputationManager(), DynaFlowProvider.getVersionCommand(config), DynaFlowConfig.DYNAFLOW_LAUNCHER_PROGRAM_NAME, true);
        List<Contingency> contingencies = contingenciesProvider.getContingencies(network);
        ReportNode dfsaReportNode = DynaflowReports.createDynaFlowSecurityAnalysisReportNode(runParameters.getReportNode(), network.getId());

        DynaFlowSecurityAnalysisHandler executionHandler = new DynaFlowSecurityAnalysisHandler(network, workingVariantId, getCommand(config), runParameters.getSecurityAnalysisParameters(), contingencies, runParameters.getFilter(), runParameters.getInterceptors(), dfsaReportNode);
        return runParameters.getComputationManager().execute(execEnv, executionHandler);
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
        List<String> args = Arrays.asList("--network", NETWORK_FILENAME,
                "--config", CONFIG_FILENAME,
                "--contingencies", CONTINGENCIES_FILENAME);
        return new SimpleCommandBuilder()
                .id("dynaflow_sa")
                .program(config.getProgram())
                .args(args)
                .build();
    }
}
