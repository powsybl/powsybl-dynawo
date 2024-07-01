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
import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.ComparisonUtils;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.dynawo.security.DynawoAlgorithmsConfig;
import com.powsybl.dynawo.security.DynawoDynamicSecurityAnalysisProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisProvider;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisRunParameters;
import com.powsybl.security.json.SecurityAnalysisResultSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class DynawoDynamicSecurityAnalysisTest extends AbstractDynawoTest {

    private DynamicSecurityAnalysisProvider provider;

    private DynamicSecurityAnalysisParameters parameters;

    private DynaWaltzParameters dynaWaltzParameters;

    @Override
    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        provider = new DynawoDynamicSecurityAnalysisProvider(new DynawoAlgorithmsConfig(Path.of("/dynaflow-launcher"), false));
        parameters = new DynamicSecurityAnalysisParameters()
                .setDynamicSimulationParameters(new DynamicSimulationParameters(0, 100))
                .setDynamicContingenciesParameters(new DynamicSecurityAnalysisParameters.ContingenciesParameters(50));
        dynaWaltzParameters = new DynaWaltzParameters();
        parameters.getDynamicSimulationParameters().addExtension(DynaWaltzParameters.class, dynaWaltzParameters);
    }

    @Test
    void testIeee14() throws IOException {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/ieee14/disconnectline/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME));

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/solvers.par"), "2");
        dynaWaltzParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynaWaltzParameters.SolverType.IDA)
                .setDefaultDumpFileParameters();

        ReportNode reportNode = ReportNode.newRootReportNode()
                .withMessageTemplate("root", "Root message")
                .build();
        List<Contingency> contingencies = List.of(Contingency.load("_LOAD__11_EC"));

        DynamicSecurityAnalysisRunParameters runParameters = new DynamicSecurityAnalysisRunParameters()
                .setComputationManager(computationManager)
                .setDynamicSecurityAnalysisParameters(parameters)
                .setReportNode(reportNode);

        SecurityAnalysisResult result = provider.run(network, VariantManagerConstants.INITIAL_VARIANT_ID,
                        dynamicModelsSupplier, n -> contingencies, runParameters)
                .join()
                .getResult();

        StringWriter swReporterAs = new StringWriter();
        reportNode.print(swReporterAs);
        InputStream refStreamReporterAs = Objects.requireNonNull(getClass().getResourceAsStream("/ieee14/dynamic-security-analysis/timeline_report.txt"));
        String refLogExportAs = TestUtil.normalizeLineSeparator(new String(ByteStreams.toByteArray(refStreamReporterAs), StandardCharsets.UTF_8));
        String logExportAs = TestUtil.normalizeLineSeparator(swReporterAs.toString());
        assertEquals(refLogExportAs, logExportAs);

        StringWriter serializedResult = new StringWriter();
        SecurityAnalysisResultSerializer.write(result, serializedResult);
        InputStream expected = Objects.requireNonNull(getClass().getResourceAsStream("/ieee14/dynamic-security-analysis/results.json"));
        ComparisonUtils.assertTxtEquals(expected, serializedResult.toString());
    }
}
