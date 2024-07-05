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
import com.powsybl.dynawo.DumpFileParameters;
import com.powsybl.dynawo.DynawoSimulationConfig;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.suppliers.dynamicmodels.DynawoModelsSupplier;
import com.powsybl.dynawo.suppliers.events.DynawoEventModelsSupplier;
import com.powsybl.dynawo.xml.ParametersXml;
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
class DynawoSimulationTest extends AbstractDynawoTest {

    private DynamicSimulationProvider provider;

    private DynamicSimulationParameters parameters;

    private DynawoSimulationParameters dynawoSimulationParameters;

    @Override
    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        provider = new DynawoSimulationProvider(new DynawoSimulationConfig(Path.of("/dynawo"), false));
        parameters = new DynamicSimulationParameters()
                .setStartTime(0)
                .setStopTime(100);
        dynawoSimulationParameters = new DynawoSimulationParameters();
        parameters.addExtension(DynawoSimulationParameters.class, dynawoSimulationParameters);
    }

    @Test
    void testIeee14() {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/ieee14/disconnectline/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
                getResourceAsStream("/ieee14/disconnectline/eventModels.groovy"),
                GroovyExtension.find(EventModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        GroovyCurvesSupplier curvesSupplier = new GroovyCurvesSupplier(
                getResourceAsStream("/ieee14/disconnectline/curves.groovy"),
                GroovyExtension.find(CurveGroovyExtension.class, DynawoSimulationProvider.NAME));

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/solvers.par"), "2");
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.IDA)
                .setTimelineExportMode(DynawoSimulationParameters.ExportMode.XML);

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
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
                getResourceAsStream("/ieee14/disconnectline/eventModels.groovy"),
                GroovyExtension.find(EventModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        GroovyCurvesSupplier curvesSupplier = new GroovyCurvesSupplier(
                getResourceAsStream("/ieee14/disconnectline/curves.groovy"),
                GroovyExtension.find(CurveGroovyExtension.class, DynawoSimulationProvider.NAME));

        Path dumpDir = Files.createDirectory(localDir.resolve("dumpFiles"));

        // Export dump
        parameters.setStopTime(30);
        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/solvers.par"), "2");
        DumpFileParameters dumpFileParameters = DumpFileParameters.createExportDumpFileParameters(dumpDir);
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.IDA)
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
        dynawoSimulationParameters.setDumpFileParameters(DumpFileParameters.createImportDumpFileParameters(dumpDir, dumpFile));

        result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, curvesSupplier,
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();

        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
    }

    @Test
    void testSvarc() {
        Network network = SvcTestCaseFactory.create();

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/svarc/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/svarc/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/svarc/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/svarc/solvers.par"), "2");
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.IDA)
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
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/hvdc/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/hvdc/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/hvdc/solvers.par"), "2");
        ReportNode reportNode = newRootReportNode().withMessageTemplate("testHvdc", "Test HVDC").build();
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.IDA)
                .setSpecificLogs(EnumSet.allOf(DynawoSimulationParameters.SpecificLog.class));

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
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
                getResourceAsStream("/smib/eventModels.groovy"),
                GroovyExtension.find(EventModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        GroovyCurvesSupplier curvesSupplier = new GroovyCurvesSupplier(
                getResourceAsStream("/smib/curves.groovy"),
                GroovyExtension.find(CurveGroovyExtension.class, DynawoSimulationProvider.NAME));

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/smib/SMIB.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/smib/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/smib/solvers.par"), "1");
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.IDA)
                .setWriteFinalState(false);

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
        Network network = Network.read(new ResourceDataSource("powsybl_dynawo", new ResourceSet("/error", "powsybl_dynawo.xiidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/error/models.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
                getResourceAsStream("/error/eventModels.groovy"),
                GroovyExtension.find(EventModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        parameters.setStopTime(200);
        dynawoSimulationParameters.setModelsParameters(ParametersXml.load(getResourceAsStream("/error/models.par")))
                .setNetworkParameters(ParametersXml.load(getResourceAsStream("/error/network.par"), "NETWORK"))
                .setSolverParameters(ParametersXml.load(getResourceAsStream("/error/solvers.par"), "3"))
                .setSolverType(DynawoSimulationParameters.SolverType.SIM);

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, CurvesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();

        assertEquals(DynamicSimulationResult.Status.FAILURE, result.getStatus());
        assertEquals("time step <= 0.1 s for more than 10 iterations ( DYNSolverCommonFixedTimeStep.cpp:419 )", result.getStatusText());
        assertTrue(result.getTimeLine().isEmpty());
        assertTrue(result.getCurves().isEmpty());
    }

    @Test
    void testIeee14DynawoSuppliers() {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        DynamicModelsSupplier dynamicModelsSupplier = DynawoModelsSupplier.load(getResourceAsStream("/ieee14/disconnectline/dynamicModels.json"));
        EventModelsSupplier eventModelsSupplier = DynawoEventModelsSupplier.load(getResourceAsStream("/ieee14/disconnectline/eventModels.json"));

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/ieee14/disconnectline/solvers.par"), "2");
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.IDA)
                .setTimelineExportMode(DynawoSimulationParameters.ExportMode.XML);

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, CurvesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();

        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getStatusText().isEmpty());
        assertEquals(0, result.getCurves().size());
        List<TimelineEvent> timeLine = result.getTimeLine();
        assertEquals(11, timeLine.size());
        checkFirstTimeLineEvent(timeLine.get(0), 0, "_GEN____8_SM", "PMIN : activation");
    }

    private void checkFirstTimeLineEvent(TimelineEvent event, double time, String modelName, String message) {
        assertEquals(time, event.time());
        assertEquals(modelName, event.modelName());
        assertEquals(message, event.message());
    }
}
