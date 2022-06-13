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
import com.powsybl.iidm.network.Network;
import com.powsybl.security.*;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;
import com.powsybl.security.monitor.StateMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(SecurityAnalysisProvider.class)
public class DynaFlowSecurityAnalysisProvider implements SecurityAnalysisProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DynaFlowSecurityAnalysisProvider.class);

    private static final String PROVIDER_NAME = "DynawoSecurityAnalysis";
    private static final String PROVIDER_VERSION = "1.0";

    @Override
    public CompletableFuture<SecurityAnalysisReport> run(Network network,
                                                         String workingVariantId,
                                                         LimitViolationDetector detector,
                                                         LimitViolationFilter filter,
                                                         ComputationManager computationManager,
                                                         SecurityAnalysisParameters parameters,
                                                         ContingenciesProvider contingenciesProvider,
                                                         List<SecurityAnalysisInterceptor> interceptors,
                                                         List<StateMonitor> monitors,
                                                         Reporter reporter) {
        if (monitors != null && !monitors.isEmpty()) {
            LOG.error("Monitoring is not possible with Dynaflow implementation. There will not be supplementary information about monitored equipment.");
        }
        if (reporter != Reporter.NO_OP) {
            LOG.warn("Reporters are not used in Dynaflow implementation");
        }
        DynaFlowSecurityAnalysis securityAnalysis = new DynaFlowSecurityAnalysis(network, detector, filter, computationManager);
        interceptors.forEach(securityAnalysis::addInterceptor);
        return securityAnalysis.run(workingVariantId, parameters, contingenciesProvider);
    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getVersion() {
        return PROVIDER_VERSION;
    }
}
