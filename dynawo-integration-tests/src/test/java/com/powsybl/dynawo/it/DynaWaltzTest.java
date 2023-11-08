/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.it;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynamicsimulation.groovy.*;
import com.powsybl.dynawaltz.DumpFileParameters;
import com.powsybl.dynawaltz.DynaWaltzConfig;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import com.powsybl.timeseries.DoubleTimeSeries;
import com.powsybl.timeseries.StringTimeSeries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Geoffroy Jamgotchian {@literal <geoffroy.jamgotchian at rte-france.com>}
 */
class DynaWaltzTest extends AbstractDynawoTest {

    private DynamicSimulationProvider provider;

    private DynamicSimulationParameters parameters;

    private DynaWaltzParameters dynaWaltzParameters;

    @Override
    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        provider = new DynaWaltzProvider(new DynaWaltzConfig(Path.of("/dynawo"), false));
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

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/solvers.par"), "2");
        dynaWaltzParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynaWaltzParameters.SolverType.IDA)
                .setDefaultDumpFileParameters();

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier,
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters)
                .join();

        assertTrue(result.isOk());
        assertEquals(41, result.getCurves().size());
        DoubleTimeSeries ts1 = (DoubleTimeSeries) result.getCurve("_GEN____1_SM_generator_UStatorPu");
        assertEquals("_GEN____1_SM_generator_UStatorPu", ts1.getMetadata().getName());
        assertEquals(587, ts1.toArray().length);
        StringTimeSeries timeLine = result.getTimeLine();
        assertEquals(1, timeLine.toArray().length);
        assertNull(timeLine.toArray()[0]); // FIXME
    }

    @Test
    void testIeee14WithDump() throws IOException {
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

        Path dumpDir = Files.createDirectory(localDir.resolve("dumpFiles"));
        Path dumpFile;

        // Export dump
        parameters.setStopTime(30);
        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/solvers.par"), "2");
        DumpFileParameters dumpFileParameters = new DumpFileParameters(true, false, dumpDir, null);
        dynaWaltzParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynaWaltzParameters.SolverType.IDA)
                .setDumpFileParameters(dumpFileParameters);

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier,
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters)
                .join();
        assertTrue(result.isOk());

        //Use exported dump as input
        parameters.setStartTime(30);
        parameters.setStopTime(100);
        try(Stream<Path> stream = Files.list(dumpDir)) {
            dumpFile = stream.findFirst().orElseThrow();
        }
        dumpFileParameters = new DumpFileParameters(false, true, dumpDir, dumpFile.getFileName().toString());
        dynaWaltzParameters.setDumpFileParameters(dumpFileParameters);

        result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier,
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters)
                .join();

        assertTrue(result.isOk());

    }

    @Test
    void testSvc() {
        Network network = SvcTestCaseFactory.create();

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/svc/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME));

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/svc/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/svc/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/svc/solvers.par"), "2");
        dynaWaltzParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynaWaltzParameters.SolverType.IDA)
                .setDefaultDumpFileParameters();

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

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/hvdc/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/hvdc/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/hvdc/solvers.par"), "2");
        dynaWaltzParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynaWaltzParameters.SolverType.IDA)
                .setDefaultDumpFileParameters();

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
    void testSmib() {
        Network network = Network.read(new ResourceDataSource("SMIB", new ResourceSet("/smib", "SMIB.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/smib/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME));

        GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
                getResourceAsStream("/smib/eventModels.groovy"),
                GroovyExtension.find(EventModelGroovyExtension.class, DynaWaltzProvider.NAME));

        GroovyCurvesSupplier curvesSupplier = new GroovyCurvesSupplier(
                getResourceAsStream("/smib/curves.groovy"),
                GroovyExtension.find(CurveGroovyExtension.class, DynaWaltzProvider.NAME));

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/smib/SMIB.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/smib/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/smib/solvers.par"), "1");
        dynaWaltzParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynaWaltzParameters.SolverType.IDA)
                .setWriteFinalState(false)
                .setDefaultDumpFileParameters();

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier,
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters)
                .join();

        assertTrue(result.isOk());
        assertEquals(35, result.getCurves().size());
        StringTimeSeries timeLine = result.getTimeLine();
        assertEquals(1, timeLine.toArray().length);
        assertNull(timeLine.toArray()[0]); // FIXME
    }
}
