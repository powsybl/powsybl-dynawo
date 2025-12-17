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
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.algorithms.DynawoAlgorithmsConfig;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
import com.powsybl.dynawo.contingency.results.Status;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationParameters;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationProvider;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationRunParameters;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultsBuilder;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultsProvider;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResult;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.ParametersXml;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
class CriticalTimeCalculationTest extends AbstractDynawoTest {

    private CriticalTimeCalculationProvider provider;

    private CriticalTimeCalculationParameters parameters;

    private DynawoSimulationParameters dynawoSimulationParameters;

    private static final List<String> GENERATORS = List.of("_GEN____1_SM", "_GEN____2_SM", "_GEN____3_SM");

    private static final List<CriticalTimeCalculationResult> EXPECTED_RESULTS = List.of(
            new CriticalTimeCalculationResult("NodeFault_0", Status.CT_BELOW_MIN_BOUND),
            new CriticalTimeCalculationResult("NodeFault_1", Status.RESULT_FOUND, 2.185),
            new CriticalTimeCalculationResult("NodeFault_2", Status.CT_ABOVE_MAX_BOUND));

    @Override
    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        computationManager = LocalComputationManager.getDefault();
        java.nio.file.Path p = Path.of("/dynaflow-launcher");
        provider = new CriticalTimeCalculationProvider(new DynawoAlgorithmsConfig(p, true));
        dynawoSimulationParameters = new DynawoSimulationParameters();
        parameters = CriticalTimeCalculationParameters.builder()
                .setStartTime(0)
                .setStopTime(5)
                .setMinValue(2.184)
                .setMaxValue(2.186)
                .setAccuracy(0.001)
                .setMode(CriticalTimeCalculationParameters.Mode.SIMPLE)
                .setDynawoParameters(dynawoSimulationParameters)
                .build();
    }

    @Test
    void testIeee14CTC() {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/ieee14/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/ieee14/critical-time-calculation/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/ieee14/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/ieee14/solvers.par"), "2");
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.IDA);

        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME,
                        PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testCTC")
                .build();

        CriticalTimeCalculationRunParameters runParameters = new CriticalTimeCalculationRunParameters()
                .setComputationManager(computationManager)
                .setCriticalTimeCalculationParameters(parameters)
                .setReportNode(reportNode);

        NodeFaultsProvider nodeFaultsProvider = (n, r) ->
                GENERATORS.stream().map(gen -> List.of(new NodeFaultsBuilder(n, r)
                                .elementId(gen)
                                .faultRPu(0.001)
                                .faultXPu(0.001)
                                .build()))
                        .toList();

        List<CriticalTimeCalculationResult> results = provider.run(network, VariantManagerConstants.INITIAL_VARIANT_ID,
                        dynamicModelsSupplier, nodeFaultsProvider, runParameters)
                .join()
                .getCriticalTimeCalculationResults();

        assertThat(results).containsExactlyElementsOf(EXPECTED_RESULTS);
    }
}
