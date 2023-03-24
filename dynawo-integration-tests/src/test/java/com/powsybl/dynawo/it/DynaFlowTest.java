/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.it;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.commons.test.ComparisonUtils;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynaflow.DynaFlowConfig;
import com.powsybl.dynaflow.DynaFlowParameters;
import com.powsybl.dynaflow.DynaFlowProvider;
import com.powsybl.dynaflow.DynaFlowSecurityAnalysisProvider;
import com.powsybl.ieeecdf.converter.IeeeCdfNetworkFactory;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.security.*;
import com.powsybl.security.detectors.DefaultLimitViolationDetector;
import com.powsybl.security.json.SecurityAnalysisResultSerializer;
import com.powsybl.security.results.PostContingencyResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
        Network network = IeeeCdfNetworkFactory.create14();
        LoadFlowResult result = loadFlowProvider.run(network, computationManager, VariantManagerConstants.INITIAL_VARIANT_ID, loadFlowParameters)
                .join();
        assertTrue(result.isOk());
        assertEquals(1, result.getComponentResults().size());
        LoadFlowResult.ComponentResult componentResult = result.getComponentResults().get(0);
        assertEquals(LoadFlowResult.ComponentResult.Status.CONVERGED, componentResult.getStatus());
        assertEquals("B4", componentResult.getSlackBusId());
    }

    @Test
    void testSa() throws IOException {
        Network network = IeeeCdfNetworkFactory.create14();

        // Changing limits to have some post-contingencies limit violations
        network.getLineStream().forEach(l -> l.newCurrentLimits1().setPermanentLimit(200.).add());
        network.getVoltageLevelStream().forEach(vl -> vl.setHighVoltageLimit(vl.getNominalV() * 1.1));
        network.getVoltageLevelStream().forEach(vl -> vl.setLowVoltageLimit(vl.getNominalV() * 0.99));

        List<Contingency> contingencies = network.getLineStream()
                .map(l -> Contingency.line(l.getId()))
                .collect(Collectors.toList());
        SecurityAnalysisResult result = securityAnalysisProvider.run(network, VariantManagerConstants.INITIAL_VARIANT_ID, new DefaultLimitViolationDetector(),
                        new LimitViolationFilter(), computationManager, securityAnalysisParameters, n -> contingencies, Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Reporter.NO_OP)
                .join()
                .getResult();

        StringWriter serializedResult = new StringWriter();
        SecurityAnalysisResultSerializer.write(result, serializedResult);
        InputStream expected = Objects.requireNonNull(getClass().getResourceAsStream("/security_analysis_result.json"));
        ComparisonUtils.compareTxt(expected, serializedResult.toString());
    }
}
