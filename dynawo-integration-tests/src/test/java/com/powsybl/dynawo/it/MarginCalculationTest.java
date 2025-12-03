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
import com.powsybl.commons.test.PowsyblTestReportResourceBundle;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
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
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawo.contingency.results.Status.CONVERGENCE;
import static com.powsybl.dynawo.contingency.results.Status.CRITERIA_NON_RESPECTED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class MarginCalculationTest extends AbstractDynawoTest {

    private MarginCalculationProvider provider;

    private MarginCalculationParameters parameters;

    private DynawoSimulationParameters dynawoSimulationParameters;

    private static final String LINE_ID = "_BUS____1-BUS____5-1_AC";

    private static final String GEN_ID = "_GEN____2_SM";

    private static final List<String> LOADS = List.of("_LOAD___3_EC", "_LOAD___6_EC", "_LOAD___9_EC");

    private static final List<LoadIncreaseResult> EXPECTED_RESULTS = List.of(
                new LoadIncreaseResult(100, CRITERIA_NON_RESPECTED, List.of(),
                        List.of(new FailedCriterion("total load power = 207.704MW > 200MW (criteria id: Risque protection)", 56.929320))),
                new LoadIncreaseResult(0, CONVERGENCE,
                        List.of(new ScenarioResult(LINE_ID, CONVERGENCE),
                                new ScenarioResult(GEN_ID, CONVERGENCE))),
                new LoadIncreaseResult(50, CONVERGENCE,
                        List.of(new ScenarioResult(LINE_ID, CONVERGENCE),
                                new ScenarioResult(GEN_ID, CONVERGENCE))),
                new LoadIncreaseResult(75, CRITERIA_NON_RESPECTED, List.of(),
                        List.of(new FailedCriterion("total load power = 200.133MW > 200MW (criteria id: Risque protection)", 55.0))),
                new LoadIncreaseResult(63, CRITERIA_NON_RESPECTED, List.of(),
                        List.of(new FailedCriterion("total load power = 232.439MW > 200MW (criteria id: Risque QdE)", 79.689024))),
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
                                new ScenarioResult(GEN_ID, CONVERGENCE))));

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

    @Test
    void testIeee14MC() {
        List<LoadIncreaseResult> results = setupIeee14MC();
        assertThat(results).containsExactlyElementsOf(EXPECTED_RESULTS);
        testExecutionTempFile();
    }

    private List<LoadIncreaseResult> setupIeee14MC() {
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
                        .getResource("/ieee14/margin-calculation/criteria.crt")).getPath()));

        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME,
                        PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testMC")
                .build();

        MarginCalculationRunParameters runParameters = new MarginCalculationRunParameters()
                .setComputationManager(computationManager)
                .setMarginCalculationParameters(parameters)
                .setReportNode(reportNode);

        List<Contingency> contingencies = List.of(
                Contingency.line(LINE_ID, "_BUS____5_VL"),
                Contingency.generator(GEN_ID));

        LoadsVariationSupplier loadsVariationSupplier = (n, r) ->
                LOADS.stream().map(load -> new LoadsVariationBuilder(n, r)
                                .loads(load)
                                .variationValue(10)
                                .build())
                        .toList();

        return provider.run(network, VariantManagerConstants.INITIAL_VARIANT_ID,
                        dynamicModelsSupplier, n -> contingencies, loadsVariationSupplier, runParameters)
                .join()
                .getLoadIncreaseResults();
    }
}
