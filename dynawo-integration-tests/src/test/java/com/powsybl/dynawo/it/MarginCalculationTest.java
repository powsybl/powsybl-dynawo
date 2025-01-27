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
import com.powsybl.dynawo.contingency.results.FailedCriterion;
import com.powsybl.dynawo.contingency.results.ScenarioResult;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.powsybl.dynawo.contingency.results.Status.CONVERGENCE;
import static com.powsybl.dynawo.contingency.results.Status.CRITERIA_NON_RESPECTED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class MarginCalculationTest extends AbstractDynawoTest {

    private MarginCalculationProvider provider;

    private MarginCalculationParameters parameters;

    private DynawoSimulationParameters dynawoSimulationParameters;

    private static final String LINE_ID = "_BUS____1-BUS____5-1_AC";

    private static final String GEN_ID = "_GEN____2_SM";

    private final List<String> loads = List.of("_LOAD___3_EC", "_LOAD___6_EC", "_LOAD___9_EC");

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
                .setLoadModelsRule(MarginCalculationParameters.LoadModelsRule.TARGETED_LOADS)
                .build();
    }

    @ParameterizedTest
    @MethodSource("provideSimulationParameter")
    void testIeee14MC(String criteriaPath, List<Contingency> contingencies, List<Integer> variations, List<LoadIncreaseResult> expectedResults) {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/ieee14/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/ieee14/margin-calculation/models.par"));
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

        LoadsVariationSupplier loadsVariationSupplier = (n, r) -> IntStream
                .range(0, loads.size())
                .mapToObj(i -> new LoadsVariationBuilder(n, r)
                        .loads(loads.get(i))
                        .variationValue(variations.get(i))
                        .build())
                .toList();

        List<LoadIncreaseResult> results = provider.run(network, VariantManagerConstants.INITIAL_VARIANT_ID,
                        dynamicModelsSupplier, n -> contingencies, loadsVariationSupplier, runParameters)
                .join()
                .getLoadIncreaseResults();

        assertThat(results).containsExactlyElementsOf(expectedResults);
    }

    private static Stream<Arguments> provideSimulationParameter() {
        return Stream.of(
                Arguments.of("/ieee14/margin-calculation/convergence/criteria.crt",
                        List.of(Contingency.line(LINE_ID, "_BUS____5_VL"),
                                Contingency.generator(GEN_ID)),
                        List.of(10, 10, 10),
                        List.of(
                            new LoadIncreaseResult(0, CONVERGENCE,
                                    List.of(new ScenarioResult(LINE_ID, CONVERGENCE),
                                            new ScenarioResult(GEN_ID, CONVERGENCE))),
                            new LoadIncreaseResult(50, CONVERGENCE,
                                    List.of(new ScenarioResult(LINE_ID, CONVERGENCE),
                                            new ScenarioResult(GEN_ID, CONVERGENCE))),
                            new LoadIncreaseResult(75, CRITERIA_NON_RESPECTED),
                            new LoadIncreaseResult(63, CRITERIA_NON_RESPECTED),
                            new LoadIncreaseResult(57, CONVERGENCE,
                                    List.of(new ScenarioResult(LINE_ID, CRITERIA_NON_RESPECTED,
                                                    List.of(new FailedCriterion("total load power = 221.465MW > 200MW (criteria id: Risque QdE)", 174.2))),
                                            new ScenarioResult(GEN_ID, CRITERIA_NON_RESPECTED,
                                                    List.of(new FailedCriterion("total load power = 216.81MW > 200MW (criteria id: Risque QdE)", 174.2))))),
                            new LoadIncreaseResult(54, CONVERGENCE,
                                    List.of(new ScenarioResult(LINE_ID, CRITERIA_NON_RESPECTED,
                                                    List.of(new FailedCriterion("total load power = 208.882MW > 200MW (criteria id: Risque QdE)", 172.4))),
                                            new ScenarioResult(GEN_ID, CONVERGENCE))),
                            new LoadIncreaseResult(52, CONVERGENCE,
                                    List.of(new ScenarioResult(LINE_ID, CONVERGENCE),
                                            new ScenarioResult(GEN_ID, CONVERGENCE)))))
//                Arguments.of("/ieee14/dynamic-security-analysis/convergence/criteria.crt",
//                        List.of(Contingency.generator(GEN_ID)),
//                        List.of(20, 10, 10),
//                        List.of())
        );
    }
}
