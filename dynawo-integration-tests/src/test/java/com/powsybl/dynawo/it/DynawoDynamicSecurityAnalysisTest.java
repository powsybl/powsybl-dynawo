/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.it;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.commons.reporter.ReporterModel;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.*;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.dynawo.security.DynawoAlgorithmsConfig;
import com.powsybl.dynawo.security.DynawoDynamicSecurityAnalysisProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.security.LimitViolationFilter;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.detectors.DefaultLimitViolationDetector;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void testIeee14() {
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

        ReporterModel reporter = new ReporterModel("root", "Root message");

        List<Contingency> contingencies = List.of(Contingency.load("_LOAD__11_EC"));

        SecurityAnalysisResult result = provider.run(network, dynamicModelsSupplier, EventModelsSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, new DefaultLimitViolationDetector(),
                        new LimitViolationFilter(), computationManager, parameters, n -> contingencies,
                        Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
                        Collections.emptyList(), reporter)
                .join()
                .getResult();

        assertNotNull(result);
    }
}
