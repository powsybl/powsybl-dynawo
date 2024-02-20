/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.it;

import com.google.common.io.ByteStreams;
import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.commons.reporter.ReporterModel;
import com.powsybl.commons.test.ComparisonUtils;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynaflow.DynaFlowConfig;
import com.powsybl.dynaflow.DynaFlowParameters;
import com.powsybl.dynaflow.DynaFlowProvider;
import com.powsybl.dynaflow.DynaFlowSecurityAnalysisProvider;
import com.powsybl.ieeecdf.converter.IeeeCdfNetworkFactory;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.iidm.network.test.FourSubstationsNodeBreakerFactory;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.security.LimitViolationFilter;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.detectors.DefaultLimitViolationDetector;
import com.powsybl.security.json.SecurityAnalysisResultSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Geoffroy Jamgotchian {@literal <geoffroy.jamgotchian at rte-france.com>}
 */
class DynaFlowTest extends AbstractDynawoTest {

    private DynaFlowProvider loadFlowProvider;

    private DynaFlowSecurityAnalysisProvider securityAnalysisProvider;

    private LoadFlowParameters loadFlowParameters;

    private SecurityAnalysisParameters securityAnalysisParameters;

    @Override
    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        DynaFlowConfig config = new DynaFlowConfig(Path.of("/dynaflow-launcher"), false);
        loadFlowProvider = new DynaFlowProvider(() -> config);
        loadFlowParameters = new LoadFlowParameters();
        securityAnalysisProvider = new DynaFlowSecurityAnalysisProvider(() -> config);
        securityAnalysisParameters = new SecurityAnalysisParameters();
        loadFlowParameters.addExtension(DynaFlowParameters.class, new DynaFlowParameters());
    }

    @Test
    void testLf() throws IOException {
        Network network = IeeeCdfNetworkFactory.create14Solved();
        network.getLine("L6-13-1").newCurrentLimits1()
                .beginTemporaryLimit().setName("1").setAcceptableDuration(60).setValue(100).endTemporaryLimit()
                .beginTemporaryLimit().setName("2").setAcceptableDuration(120).setValue(110).endTemporaryLimit()
                .setPermanentLimit(200)
                .add();

        ReporterModel reporter = new ReporterModel("root", "testLf root reporter");
        LoadFlowResult result = loadFlowProvider.run(network, computationManager, VariantManagerConstants.INITIAL_VARIANT_ID, loadFlowParameters, reporter)
                .join();

        assertTrue(result.isOk());
        assertEquals(1, result.getComponentResults().size());
        LoadFlowResult.ComponentResult componentResult = result.getComponentResults().get(0);
        assertEquals(LoadFlowResult.ComponentResult.Status.CONVERGED, componentResult.getStatus());
        assertEquals("B4", componentResult.getSlackBusId());

        StringWriter sw = new StringWriter();
        reporter.export(sw);
        System.out.println(sw);
        InputStream refStream = Objects.requireNonNull(getClass().getResourceAsStream("/loadflow_timeline_report.txt"));
        String refLogExport = TestUtil.normalizeLineSeparator(new String(ByteStreams.toByteArray(refStream), StandardCharsets.UTF_8));
        String logExport = TestUtil.normalizeLineSeparator(sw.toString());
        assertEquals(refLogExport, logExport);
    }

    @Test
    void testSaBb() throws IOException {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        // Changing limits to have some pre- and post-contingencies limit violations
        network.getLine("_BUS____1-BUS____5-1_AC").newCurrentLimits1().setPermanentLimit(500.).add();
        network.getLine("_BUS____1-BUS____2-1_AC").newCurrentLimits1()
                .beginTemporaryLimit().setName("tl").setAcceptableDuration(120).setValue(1200).endTemporaryLimit()
                .setPermanentLimit(1500.)
                .add();
        network.getVoltageLevelStream().forEach(vl -> vl.setHighVoltageLimit(vl.getNominalV() * 1.09));
        network.getVoltageLevelStream().forEach(vl -> vl.setLowVoltageLimit(vl.getNominalV() * 0.97));

        // Launching a load flow before the security analysis is required
        ReporterModel reporterLf = new ReporterModel("root", "Root message");
        loadFlowProvider.run(network, computationManager, VariantManagerConstants.INITIAL_VARIANT_ID, loadFlowParameters, reporterLf).join();

        StringWriter swReporterLf = new StringWriter();
        reporterLf.export(swReporterLf);
        InputStream refStreamLf = Objects.requireNonNull(getClass().getResourceAsStream("/ieee14/security-analysis/timeline_report_lf.txt"));
        String refLogExportLf = TestUtil.normalizeLineSeparator(new String(ByteStreams.toByteArray(refStreamLf), StandardCharsets.UTF_8));
        String logExportLf = TestUtil.normalizeLineSeparator(swReporterLf.toString());
        assertEquals(refLogExportLf, logExportLf);

        List<Contingency> contingencies = network.getLineStream()
                .map(l -> Contingency.line(l.getId()))
                .toList();

        ReporterModel reporter = new ReporterModel("root", "Root message");
        SecurityAnalysisResult result = securityAnalysisProvider.run(network, VariantManagerConstants.INITIAL_VARIANT_ID, new DefaultLimitViolationDetector(),
                        new LimitViolationFilter(), computationManager, securityAnalysisParameters, n -> contingencies, Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), reporter)
                .join()
                .getResult();

        StringWriter swReporterAs = new StringWriter();
        reporter.export(swReporterAs);
        InputStream refStreamReporterAs = Objects.requireNonNull(getClass().getResourceAsStream("/ieee14/security-analysis/timeline_report_as.txt"));
        String refLogExportAs = TestUtil.normalizeLineSeparator(new String(ByteStreams.toByteArray(refStreamReporterAs), StandardCharsets.UTF_8));
        String logExportAs = TestUtil.normalizeLineSeparator(swReporterAs.toString());
        assertEquals(refLogExportAs, logExportAs);

        StringWriter serializedResult = new StringWriter();
        SecurityAnalysisResultSerializer.write(result, serializedResult);
        InputStream expected = Objects.requireNonNull(getClass().getResourceAsStream("/ieee14/security-analysis/sa_bb_results.json"));
        ComparisonUtils.compareTxt(expected, serializedResult.toString());
    }

    @Test
    void testSaNb() throws IOException {
        Network network = FourSubstationsNodeBreakerFactory.create();

        List<Contingency> contingencies = network.getGeneratorStream()
                .map(g -> Contingency.generator(g.getId()))
                .toList();
        SecurityAnalysisResult result = securityAnalysisProvider.run(network, VariantManagerConstants.INITIAL_VARIANT_ID, new DefaultLimitViolationDetector(),
                        new LimitViolationFilter(), computationManager, securityAnalysisParameters, n -> contingencies, Collections.emptyList(),
                        Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Reporter.NO_OP)
                .join()
                .getResult();

        StringWriter serializedResult = new StringWriter();
        SecurityAnalysisResultSerializer.write(result, serializedResult);
        InputStream expected = Objects.requireNonNull(getClass().getResourceAsStream("/ieee14/security-analysis/sa_nb_results.json"));
        ComparisonUtils.compareTxt(expected, serializedResult.toString());
    }
}
