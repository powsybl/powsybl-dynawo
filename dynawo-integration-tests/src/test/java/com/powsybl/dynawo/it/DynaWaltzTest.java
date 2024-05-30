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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static com.powsybl.commons.report.ReportNode.NO_OP;
import static com.powsybl.commons.report.ReportNode.newRootReportNode;
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
                .setStartTime(0)
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
                .setDefaultDumpFileParameters()
                .setTimelineExportMode(DynaWaltzParameters.ExportMode.XML);

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier,
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();

        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getStatusText().isEmpty());
        assertEquals(41, result.getCurves().size());
        DoubleTimeSeries ts1 = result.getCurve("_GEN____1_SM_generator_UStatorPu");
        assertEquals("_GEN____1_SM_generator_UStatorPu", ts1.getMetadata().getName());
        assertEquals(587, ts1.toArray().length);
        List<TimelineEvent> timeLine = result.getTimeLine();
        assertEquals(23, timeLine.size());
        checkFirstTimeLineEvent(timeLine.get(0), 0, "_GEN____8_SM", "PMIN : activation");
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

        // Export dump
        parameters.setStopTime(30);
        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/solvers.par"), "2");
        DumpFileParameters dumpFileParameters = DumpFileParameters.createExportDumpFileParameters(dumpDir);
        dynaWaltzParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynaWaltzParameters.SolverType.IDA)
                .setDumpFileParameters(dumpFileParameters);

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier,
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();
        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());

        //Use exported dump as input
        parameters.setStartTime(30);
        parameters.setStopTime(100);

        String dumpFile;
        try (Stream<Path> stream = Files.list(dumpDir)) {
            dumpFile = stream.findFirst().map(Path::getFileName).map(Path::toString).orElseThrow();
        }
        dynaWaltzParameters.setDumpFileParameters(DumpFileParameters.createImportDumpFileParameters(dumpDir, dumpFile));

        result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier,
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();

        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
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
                .setDefaultDumpFileParameters()
                .setPrecision(10e-8);

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, EventModelsSupplier.empty(), CurvesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();

        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getStatusText().isEmpty());
        assertTrue(result.getCurves().isEmpty());
        List<TimelineEvent> timeLine = result.getTimeLine();
        assertEquals(1, timeLine.size());
        checkFirstTimeLineEvent(timeLine.get(0), 0, "G1", "PMIN : activation");
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
        ReportNode reportNode = newRootReportNode().withMessageTemplate("testHvdc", "Test HVDC").build();
        dynaWaltzParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynaWaltzParameters.SolverType.IDA)
                .setDefaultDumpFileParameters()
                .setSpecificLogs(EnumSet.allOf(DynaWaltzParameters.SpecificLog.class));

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, EventModelsSupplier.empty(), CurvesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, reportNode)
                .join();

        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getStatusText().isEmpty());
        assertTrue(result.getCurves().isEmpty());
        List<TimelineEvent> timeLine = result.getTimeLine();
        assertEquals(7, timeLine.size());
        checkFirstTimeLineEvent(timeLine.get(0), 30.0, "_BUS____5-BUS____6-1_PS", "Tap +1");
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
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();

        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getStatusText().isEmpty());
        assertEquals(35, result.getCurves().size());
        List<TimelineEvent> timeLine = result.getTimeLine();
        assertTrue(timeLine.isEmpty());
    }

    @Test
    void testSimulationError() {
        Network network = Network.read(new ResourceDataSource("powsybl_dynawaltz", new ResourceSet("/error", "powsybl_dynawaltz.xiidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/error/models.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME));

        GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
                getResourceAsStream("/error/eventModels.groovy"),
                GroovyExtension.find(EventModelGroovyExtension.class, DynaWaltzProvider.NAME));

        parameters.setStopTime(200);
        dynaWaltzParameters.setModelsParameters(ParametersXml.load(getResourceAsStream("/error/models.par")))
                .setNetworkParameters(ParametersXml.load(getResourceAsStream("/error/network.par"), "NETWORK"))
                .setSolverParameters(ParametersXml.load(getResourceAsStream("/error/solvers.par"), "3"))
                .setSolverType(DynaWaltzParameters.SolverType.SIM)
                .setDefaultDumpFileParameters();

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, CurvesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();

        assertEquals(DynamicSimulationResult.Status.FAILURE, result.getStatus());
        assertEquals("time step <= 0.1 s for more than 10 iterations ( DYNSolverCommonFixedTimeStep.cpp:419 )", result.getStatusText());
        assertTrue(result.getTimeLine().isEmpty());
        assertTrue(result.getCurves().isEmpty());
    }

    private void checkFirstTimeLineEvent(TimelineEvent event, double time, String modelName, String message) {
        assertEquals(time, event.time());
        assertEquals(modelName, event.modelName());
        assertEquals(message, event.message());
    }
}
