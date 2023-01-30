/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.auto.service.AutoService;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.computation.ComputationManager;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.dynawo.commons.PowsyblDynawoConstants;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.*;
import com.powsybl.security.action.Action;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;
import com.powsybl.security.monitor.StateMonitor;
import com.powsybl.security.strategy.OperatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.powsybl.dynaflow.DynaFlowConstants.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(SecurityAnalysisProvider.class)
public class DynaFlowSecurityAnalysisProvider implements SecurityAnalysisProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DynaFlowSecurityAnalysisProvider.class);

    private final Supplier<DynaFlowConfig> configSupplier;

    public DynaFlowSecurityAnalysisProvider() {
        this(DynaFlowConfig::fromPropertyFile);
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
                                                         Reporter reporter) {
        if (monitors != null && !monitors.isEmpty()) {
            LOG.error("Monitoring is not possible with Dynaflow implementation. There will not be supplementary information about monitored equipment.");
        }
        if (reporter != Reporter.NO_OP) {
            LOG.warn("Reporters are not used in Dynaflow implementation");
        }
        if (operatorStrategies != null && !operatorStrategies.isEmpty()) {
            LOG.error("Strategies are not implemented in Dynaflow");
        }
        if (actions != null && !actions.isEmpty()) {
            LOG.error("Actions are not implemented in Dynaflow");
        }
        DynaFlowSecurityAnalysis securityAnalysis = new DynaFlowSecurityAnalysis(network, detector, filter, computationManager, configSupplier);
        interceptors.forEach(securityAnalysis::addInterceptor);
        return securityAnalysis.run(workingVariantId, parameters, contingenciesProvider);
    }

    @Override
    public String getName() {
        return DYNAFLOW_NAME;
    }

    @Override
    public String getVersion() {
        return PowsyblDynawoConstants.getProviderVersion();
    }
}
