/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.it;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynaflow.DynaFlowConfig;
import com.powsybl.dynaflow.DynaFlowParameters;
import com.powsybl.dynaflow.DynaFlowProvider;
import com.powsybl.dynaflow.DynaFlowSecurityAnalysisProvider;
import com.powsybl.ieeecdf.converter.IeeeCdfNetworkFactory;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.security.LimitViolationFilter;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.detectors.DefaultLimitViolationDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class DynaFlowTest extends AbstractDynawoTest {

    private DynaFlowProvider loadFlowProvider;

    private DynaFlowSecurityAnalysisProvider securityAnalysisProvider;

    private LoadFlowParameters loadFlowParameters;

    private DynaFlowParameters dynaFlowLoadFlowParameters;

    private SecurityAnalysisParameters securityAnalysisParameters;

    @Override
    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        DynaFlowConfig config = new DynaFlowConfig(Path.of("/dynaflow-launcher"), false);
        loadFlowProvider = new DynaFlowProvider(() -> config);
        loadFlowParameters = new LoadFlowParameters();
        dynaFlowLoadFlowParameters = new DynaFlowParameters();
        securityAnalysisProvider = new DynaFlowSecurityAnalysisProvider(() -> config);
        securityAnalysisParameters = new SecurityAnalysisParameters();
        loadFlowParameters.addExtension(DynaFlowParameters.class, dynaFlowLoadFlowParameters);
    }

    @Test
    void testLf() {
        Network network = IeeeCdfNetworkFactory.create14Solved();
        LoadFlowResult result = loadFlowProvider.run(network, computationManager, VariantManagerConstants.INITIAL_VARIANT_ID, loadFlowParameters)
                .join();
        assertTrue(result.isOk());
        assertEquals(1, result.getComponentResults().size());
        LoadFlowResult.ComponentResult componentResult = result.getComponentResults().get(0);
        assertEquals(LoadFlowResult.ComponentResult.Status.CONVERGED, componentResult.getStatus());
        assertEquals("B4", componentResult.getSlackBusId());
    }

    @Test
    void testSa() {
        Network network = IeeeCdfNetworkFactory.create14Solved();
        List<Contingency> contingencies = network.getLineStream()
                .map(l -> Contingency.line(l.getId()))
                .collect(Collectors.toList());
        SecurityAnalysisResult result = securityAnalysisProvider.run(network, VariantManagerConstants.INITIAL_VARIANT_ID, new DefaultLimitViolationDetector(),
                        new LimitViolationFilter(), computationManager, securityAnalysisParameters, n -> contingencies, Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Reporter.NO_OP)
                .join()
                .getResult();
        assertEquals(LoadFlowResult.ComponentResult.Status.CONVERGED, result.getPreContingencyResult().getStatus());
    }
}
