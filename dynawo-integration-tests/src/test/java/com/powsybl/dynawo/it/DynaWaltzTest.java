/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.it;

import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynamicsimulation.groovy.*;
import com.powsybl.dynawaltz.DynaWaltzConfig;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import com.powsybl.timeseries.DoubleTimeSeries;
import com.powsybl.timeseries.StringTimeSeries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
class DynaWaltzTest extends AbstractDynawoTest {

    private DynamicSimulationProvider provider;

    private DynamicSimulationParameters parameters;

    private DynaWaltzParameters dynaWaltzParameters;

    @Override
    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        provider = new DynaWaltzProvider(PlatformConfig.defaultConfig(), new DynaWaltzConfig("/dynawo", false));
        parameters = new DynamicSimulationParameters()
                .setStartTime(1)
                .setStopTime(100);
        dynaWaltzParameters = new DynaWaltzParameters();
        parameters.addExtension(DynaWaltzParameters.class, dynaWaltzParameters);
    }

    @Test
    void testIeee14() {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/ieee14/disconnectline/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME));

        GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
                getResourceAsStream("/ieee14/disconnectline/eventModels.groovy"),
                GroovyExtension.find(EventModelGroovyExtension.class, DynaWaltzProvider.NAME));

        GroovyCurvesSupplier curvesSupplier = new GroovyCurvesSupplier(
                getResourceAsStream("/ieee14/disconnectline/curves.groovy"),
                GroovyExtension.find(CurveGroovyExtension.class, DynaWaltzProvider.NAME));

        dynaWaltzParameters.setParameters(getResourceAsStream("/ieee14/disconnectline/models.par"))
                .setNetwork(new DynaWaltzParameters.Network()
                        .setParametersId("8")
                        .setParameters(getResourceAsStream("/ieee14/disconnectline/network.par")))
                .setSolver(new DynaWaltzParameters.Solver()
                        .setType(DynaWaltzParameters.SolverType.IDA)
                        .setParametersId("2")
                        .setParameters(getResourceAsStream("/ieee14/disconnectline/solvers.par")));

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier,
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters)
                .join();

        assertTrue(result.isOk());
        assertEquals(41, result.getCurves().size());
        DoubleTimeSeries ts1 = (DoubleTimeSeries) result.getCurve("_GEN____1_SM_generator_UStatorPu");
        assertEquals("_GEN____1_SM_generator_UStatorPu", ts1.getMetadata().getName());
        assertEquals(586, ts1.toArray().length);
        StringTimeSeries timeLine = result.getTimeLine();
        assertEquals(1, timeLine.toArray().length);
        assertNull(timeLine.toArray()[0]); // FIXME
    }

    @Test
    void testSvc() {
        Network network = SvcTestCaseFactory.create();

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/svc/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME));

        dynaWaltzParameters.setParameters(getResourceAsStream("/svc/models.par"))
                .setNetwork(new DynaWaltzParameters.Network()
                        .setParametersId("8")
                        .setParameters(getResourceAsStream("/svc/network.par")))
                .setSolver(new DynaWaltzParameters.Solver()
                        .setType(DynaWaltzParameters.SolverType.IDA)
                        .setParametersId("2")
                        .setParameters(getResourceAsStream("/svc/solvers.par")));

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, EventModelsSupplier.empty(), CurvesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters)
                .join();

        assertTrue(result.isOk());
        assertEquals(0, result.getCurves().size());
        StringTimeSeries timeLine = result.getTimeLine();
        assertEquals(1, timeLine.toArray().length);
        assertNull(timeLine.toArray()[0]); // FIXME
    }

    @Test
    void testHvdc() {
        Network network = Network.read(new ResourceDataSource("HvdcPowerTransfer", new ResourceSet("/hvdc", "HvdcPowerTransfer.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/hvdc/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME));

        dynaWaltzParameters.setParameters(getResourceAsStream("/hvdc/models.par"))
                .setNetwork(new DynaWaltzParameters.Network()
                        .setParametersId("8")
                        .setParameters(getResourceAsStream("/hvdc/network.par")))
                .setSolver(new DynaWaltzParameters.Solver()
                        .setType(DynaWaltzParameters.SolverType.IDA)
                        .setParametersId("2")
                        .setParameters(getResourceAsStream("/hvdc/solvers.par")));

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, EventModelsSupplier.empty(), CurvesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters)
                .join();

        assertTrue(result.isOk());
        assertEquals(0, result.getCurves().size());
        StringTimeSeries timeLine = result.getTimeLine();
        assertEquals(1, timeLine.toArray().length);
        assertNull(timeLine.toArray()[0]); // FIXME
    }
}
