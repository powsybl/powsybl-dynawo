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
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.DynamicSimulationProvider;
import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynamicsimulation.groovy.*;
import com.powsybl.dynawaltz.DynaWaltzConfig;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.timeseries.DoubleTimeSeries;
import com.powsybl.timeseries.StringTimeSeries;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class DynaWaltzTest extends AbstractDynawoTest {

    private DynamicSimulationProvider provider;

    private DynamicSimulationParameters parameters;

    private DynaWaltzParameters dynaWaltzParameters;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        provider = new DynaWaltzProvider(PlatformConfig.defaultConfig(), new DynaWaltzConfig("/dynawo", false));
        parameters = new DynamicSimulationParameters()
                .setStartTime(1)
                .setStopTime(100);
        dynaWaltzParameters = new DynaWaltzParameters();
        parameters.addExtension(DynaWaltzParameters.class, dynaWaltzParameters);
    }

    @Test
    public void test() throws IOException {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14-disconnectline", "IEEE14.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/ieee14-disconnectline/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME));

        GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
                getResourceAsStream("/ieee14-disconnectline/eventModels.groovy"),
                GroovyExtension.find(EventModelGroovyExtension.class, DynaWaltzProvider.NAME));

        GroovyCurvesSupplier curvesSupplier = new GroovyCurvesSupplier(
                getResourceAsStream("/ieee14-disconnectline/curves.groovy"),
                GroovyExtension.find(CurveGroovyExtension.class, DynaWaltzProvider.NAME));

        // FIXME waiting for being able to pass parameters as an input stream
        for (String parFileName : List.of("models.par", "network.par", "solvers.par")) {
            Files.copy(getResourceAsStream("/ieee14-disconnectline/" + parFileName), localDir.resolve(parFileName));
        }

        // FIXME this should not be dependent of the run, all par file should be provider through an input stream
        dynaWaltzParameters.setParametersFile(localDir.resolve("models.par").toString())
                .setNetwork(new DynaWaltzParameters.Network()
                        .setParametersId("8")
                        .setParametersFile(localDir.resolve("network.par").toString()))
                .setSolver(new DynaWaltzParameters.Solver()
                        .setType(DynaWaltzParameters.SolverType.IDA)
                        .setParametersId("2")
                        .setParametersFile(localDir.resolve("solvers.par").toString()));

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
        assertNull(timeLine.toArray()[0]); // FIXME why null????
    }
}