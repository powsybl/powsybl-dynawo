/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.it;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynaflow.results.ScenarioResult;
import com.powsybl.dynaflow.results.Status;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.algorithms.DynawoAlgorithmsConfig;
import com.powsybl.dynawo.margincalculation.MarginCalculationParameters;
import com.powsybl.dynawo.margincalculation.MarginCalculationProvider;
import com.powsybl.dynawo.margincalculation.MarginCalculationRunParameters;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariationBuilder;
import com.powsybl.dynawo.margincalculation.loadsvariation.supplier.LoadsVariationSupplier;
import com.powsybl.dynawo.margincalculation.results.LoadIncreaseResult;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.ParametersXml;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class MarginCalculationTest extends AbstractDynawoTest {

    private MarginCalculationProvider provider;

    private MarginCalculationParameters parameters;

    private DynawoSimulationParameters dynawoSimulationParameters;

    @Override
    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        provider = new MarginCalculationProvider(new DynawoAlgorithmsConfig(Path.of("/dynaflow-launcher"), true));
        dynawoSimulationParameters = new DynawoSimulationParameters();
        parameters = MarginCalculationParameters.builder()
                .setStartTime(0)
                .setLoadIncreaseStartTime(10)
                .setLoadIncreaseStopTime(70)
                .setMarginCalculationStartTime(100)
                .setContingenciesStartTime(110)
                .setStopTime(200)
                .setDynawoParameters(dynawoSimulationParameters)
                .build();
    }

    @ParameterizedTest
    @MethodSource("provideSimulationParameter")
    void testIeee14MC(String criteriaPath, List<Contingency> contingencies) {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/ieee14/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/ieee14/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/ieee14/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/ieee14/solvers.par"), "2");
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.IDA)
                .setCriteriaFilePath(Path.of(Objects.requireNonNull(getClass()
                        .getResource(criteriaPath)).getPath()));

        ReportNode reportNode = ReportNode.newRootReportNode()
                .withMessageTemplate("mc_test", "Margin calculation integration test")
                .build();

        MarginCalculationRunParameters runParameters = new MarginCalculationRunParameters()
                .setComputationManager(computationManager)
                .setMarginCalculationParameters(parameters)
                .setReportNode(reportNode);

        LoadsVariationSupplier loadsVariationSupplier = (n, r) -> List.of(
                new LoadsVariationBuilder(n, r)
                    .loads("_LOAD___3_EC", "_LOAD___6_EC", "_LOAD___9_EC")
                    .variationValue(10)
                    .build());

        List<LoadIncreaseResult> results = provider.run(network, VariantManagerConstants.INITIAL_VARIANT_ID,
                        dynamicModelsSupplier, n -> contingencies, loadsVariationSupplier, runParameters)
                .join()
                .getLoadIncreaseResults();

        List<ScenarioResult> expectedResults = contingencies.stream()
                .map(c -> new ScenarioResult(c.getId(), Status.CONVERGENCE))
                .toList();
        assertThat(results).containsExactly(
                new LoadIncreaseResult(0, Status.CONVERGENCE, expectedResults),
                new LoadIncreaseResult(50, Status.CONVERGENCE, expectedResults),
                new LoadIncreaseResult(75, Status.CONVERGENCE, expectedResults),
                new LoadIncreaseResult(88, Status.CONVERGENCE, expectedResults),
                new LoadIncreaseResult(94, Status.CONVERGENCE, expectedResults),
                new LoadIncreaseResult(97, Status.CONVERGENCE, expectedResults),
                new LoadIncreaseResult(99, Status.CONVERGENCE, expectedResults));
    }

    private static Stream<Arguments> provideSimulationParameter() {
        return Stream.of(
                Arguments.of("/ieee14/dynamic-security-analysis/convergence/criteria.crt",
                        List.of(Contingency.line("_BUS____1-BUS____5-1_AC", "_BUS____5_VL"),
                                Contingency.generator("_GEN____2_SM")))
        );
    }
}
