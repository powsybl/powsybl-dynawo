/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.it;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.commons.report.PowsyblCoreReportResourceBundle;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.ComparisonUtils;
import com.powsybl.commons.test.PowsyblTestReportResourceBundle;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.algorithms.DynawoAlgorithmsConfig;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.security.DynawoSecurityAnalysisProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisProvider;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisRunParameters;
import com.powsybl.security.json.SecurityAnalysisResultSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynawoSecurityAnalysisTest extends AbstractDynawoTest {

    private DynamicSecurityAnalysisProvider provider;

    private DynamicSecurityAnalysisParameters parameters;

    private DynawoSimulationParameters dynawoSimulationParameters;

    @Override
    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        provider = new DynawoSecurityAnalysisProvider(new DynawoAlgorithmsConfig(Path.of("/dynaflow-launcher"), true));
        parameters = new DynamicSecurityAnalysisParameters()
                .setDynamicSimulationParameters(new DynamicSimulationParameters(0, 100))
                .setDynamicContingenciesParameters(new DynamicSecurityAnalysisParameters.ContingenciesParameters(10));
        dynawoSimulationParameters = new DynawoSimulationParameters();
        parameters.getDynamicSimulationParameters().addExtension(DynawoSimulationParameters.class, dynawoSimulationParameters);
    }

    @ParameterizedTest
    @MethodSource("provideSimulationParameter")
    void testIeee14DSA(String criteriaPath, List<Contingency> contingencies,
                       EventModelsSupplier eventModelsSupplier, String resultsPath) throws IOException {

        SecurityAnalysisResult result = runDynawoSecurityAnalysis(criteriaPath, contingencies, eventModelsSupplier);

        StringWriter serializedResult = new StringWriter();
        SecurityAnalysisResultSerializer.write(result, serializedResult);

        InputStream expected = Objects.requireNonNull(getClass().getResourceAsStream(resultsPath));
        ComparisonUtils.assertTxtEquals(expected, serializedResult.toString());
    }

    private static Stream<Arguments> provideSimulationParameter() {
        return Stream.of(
                Arguments.of("/ieee14/dynamic-security-analysis/convergence/criteria.crt",
                        List.of(Contingency.line("_BUS____1-BUS____5-1_AC", "_BUS____5_VL"),
                                Contingency.generator("_GEN____2_SM")),
                        EventModelsSupplier.empty(),
                        "/ieee14/dynamic-security-analysis/convergence/results.json"),
                Arguments.of("/ieee14/dynamic-security-analysis/failed-criteria/criteria.crt",
                        List.of(Contingency.line("_BUS____1-BUS____5-1_AC", "_BUS____5_VL")),
                        EventModelsSupplier.empty(),
                        "/ieee14/dynamic-security-analysis/failed-criteria/results.json"),
                Arguments.of("/ieee14/dynamic-security-analysis/divergence/criteria.crt",
                        List.of(Contingency.builder("Disconnect")
                                .addLine("_BUS____1-BUS____5-1_AC", "_BUS____5_VL")
                                .addGenerator("_GEN____2_SM")
                                .addBus("_BUS____1_TN")
                                .build()),
                        EventModelsSupplier.empty(),
                        "/ieee14/dynamic-security-analysis/divergence/results.json"),
                Arguments.of("/ieee14/dynamic-security-analysis/convergence/criteria.crt",
                        List.of(Contingency.line("_BUS____1-BUS____5-1_AC", "_BUS____5_VL"),
                                Contingency.generator("_GEN____2_SM")),
                        (EventModelsSupplier) (n, r) -> List.of(
                                EventDisconnectionBuilder.of(n, r)
                                        .staticId("_BUS____9-BUS___10-1_AC")
                                        .startTime(10)
                                        .build()),
                        "/ieee14/dynamic-security-analysis/convergence/results.json")
        );
    }

    private SecurityAnalysisResult runDynawoSecurityAnalysis(String criteriaPath,
                                                       List<Contingency> contingencies,
                                                       EventModelsSupplier eventModelsSupplier) throws IOException {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/ieee14/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        dynawoSimulationParameters.setModelsParameters(getResourceAsStream("/ieee14/models.par"))
                .setNetworkParameters(getResourceAsStream("/ieee14/network.par"), "8")
                .setSolverParameters(getResourceAsStream("/ieee14/solvers.par"), "2")
                .setSolverType(DynawoSimulationParameters.SolverType.IDA)
                .setCriteriaFilePath(Path.of(Objects.requireNonNull(getClass().getResource(criteriaPath)).getPath()));

        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME,
                        PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testIEEE14")
                .build();

        DynamicSecurityAnalysisRunParameters runParameters = new DynamicSecurityAnalysisRunParameters()
                .setComputationManager(computationManager)
                .setDynamicSecurityAnalysisParameters(parameters)
                .setEventModelsSupplier(eventModelsSupplier)
                .setReportNode(reportNode);

        return provider.run(network, VariantManagerConstants.INITIAL_VARIANT_ID,
                        dynamicModelsSupplier, n -> contingencies, runParameters)
                .join()
                .getResult();
    }

    @Test
    void testExecutionTempFileAndReferencedFileExist() throws IOException {
        runDynawoSecurityAnalysis(
                "/ieee14/dynamic-security-analysis/convergence/criteria.crt",
                List.of(Contingency.line("_BUS____1-BUS____5-1_AC", "_BUS____5_VL")),
                EventModelsSupplier.empty()
        );
        Path execTmpDir = localDir.getParent();
        Path execTmpFilePath = execTmpDir.resolve(".EXEC_TMP_FILENAME");
        String content = Files.readString(execTmpFilePath);
        Path referencedFile = Paths.get(content.trim());

        assertTrue(Files.exists(execTmpFilePath));
        assertNotNull(content);
        assertFalse(content.isBlank());
        assertTrue(Files.exists(referencedFile));
    }
}
