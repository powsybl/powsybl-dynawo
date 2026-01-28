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
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynamicsimulation.groovy.*;
import com.powsybl.dynawo.DumpFileParameters;
import com.powsybl.dynawo.DynawoSimulationConfig;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.commons.ExportMode;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
import com.powsybl.dynawo.models.automationsystems.TapChangerBlockingAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.dynawo.models.events.EventActivePowerVariationBuilder;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.events.NodeFaultEventBuilder;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.suppliers.dynamicmodels.DynawoModelsSupplier;
import com.powsybl.dynawo.suppliers.events.DynawoEventModelsSupplier;
import com.powsybl.dynawo.xml.ParametersXml;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.FourSubstationsNodeBreakerFactory;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import com.powsybl.timeseries.DoubleTimeSeries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.powsybl.commons.report.ReportNode.NO_OP;
import static com.powsybl.commons.report.ReportNode.newRootReportNode;
import static com.powsybl.dynawo.commons.DynawoConstants.NETWORK_FILENAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        provider = new DynawoSimulationProvider(new DynawoSimulationConfig(Path.of("/dynaflow-launcher"), true));
        parameters = new DynamicSimulationParameters()
                .setStartTime(0)
                .setStopTime(100);
        dynawoSimulationParameters = DynawoSimulationParameters.load();
        parameters.addExtension(DynawoSimulationParameters.class, dynawoSimulationParameters);
    }

    @Test
    void testIeee14() {
        DynamicSimulationResult result = setupIEEE14Simulation().get();

        testExecutionTempFile();
        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getStatusText().isEmpty());
        assertEquals(27, result.getCurves().size());
        DoubleTimeSeries ts1 = result.getCurve("_GEN____1_SM_generator_UStatorPu");
        assertEquals("_GEN____1_SM_generator_UStatorPu", ts1.getMetadata().getName());
        assertEquals(585, ts1.getMetadata().getIndex().getPointCount());
        assertEquals(14, result.getFinalStateValues().size());
        assertEquals(1.046227, result.getFinalStateValues().get("NETWORK__BUS___10_TN_Upu_value"));
        List<TimelineEvent> timeLine = result.getTimeLine();
        assertEquals(23, timeLine.size());
        checkTimeLineEvent(timeLine.getFirst(), 0, "_GEN____8_SM", "PMIN : activation");
    }

    @Test
    void testIeee14WithDump() throws IOException {
        // Export dump
        Supplier<DynamicSimulationResult> resultSupplier = setupIEEE14Simulation();
        parameters.setStopTime(30);
        Path dumpDir = Files.createDirectory(localDir.resolve("dumpFiles"));
        DumpFileParameters dumpFileParameters = DumpFileParameters.createExportDumpFileParameters(dumpDir);
        dynawoSimulationParameters.setDumpFileParameters(dumpFileParameters);
        DynamicSimulationResult result = resultSupplier.get();
        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());

        //Use exported dump as input
        parameters.setStartTime(30);
        parameters.setStopTime(100);

        String dumpFile;
        try (Stream<Path> stream = Files.list(dumpDir)) {
            dumpFile = stream.findFirst().map(Path::getFileName).map(Path::toString).orElseThrow();
        }
        dynawoSimulationParameters.setDumpFileParameters(DumpFileParameters.createImportDumpFileParameters(dumpDir, dumpFile));

        result = resultSupplier.get();

        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
    }

    @Test
    void testIeee14WithSimulationCriteria() {
        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME,
                        PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testIEEE14")
                .build();
        Supplier<DynamicSimulationResult> resultSupplier = setupIEEE14Simulation(reportNode);
        dynawoSimulationParameters.setCriteriaFilePath(Path.of(Objects.requireNonNull(getClass().getResource("/ieee14/criteria.crt")).getPath()));
        DynamicSimulationResult result = resultSupplier.get();

        assertEquals(DynamicSimulationResult.Status.FAILURE, result.getStatus());
        ReportNode dynawoLog = reportNode.getChildren().get(2);
        assertEquals("dynawo.commons.dynawoLog", dynawoLog.getMessageKey());
        assertTrue(dynawoLog.getChildren().stream().anyMatch(r -> r.getMessage().contains("one simulation's criteria is not respected")));
    }

    @Test
    void testSvarc() {
        Network network = SvcTestCaseFactory.create();

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/svarc/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        dynawoSimulationParameters.setModelsParameters(getResourceAsStream("/svarc/models.par"))
                .setNetworkParameters(getResourceAsStream("/svarc/network.par"), "8")
                .setSolverParameters(getResourceAsStream("/svarc/solvers.par"), "2")
                .setSolverType(DynawoSimulationParameters.SolverType.IDA);

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, EventModelsSupplier.empty(), OutputVariablesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();

        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getStatusText().isEmpty());
        assertTrue(result.getCurves().isEmpty());
        List<TimelineEvent> timeLine = result.getTimeLine();
        assertEquals(1, timeLine.size());
        checkTimeLineEvent(timeLine.getFirst(), 0, "G1", "PMIN : activation");
    }

    @Test
    void testHvdc() {
        Network network = Network.read(new ResourceDataSource("HvdcPowerTransfer", new ResourceSet("/hvdc", "HvdcPowerTransfer.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/hvdc/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        ReportNode reportNode = newRootReportNode()
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME,
                        PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testHvdc")
                .build();
        dynawoSimulationParameters.setModelsParameters(getResourceAsStream("/hvdc/models.par"))
                .setNetworkParameters(getResourceAsStream("/hvdc/network.par"), "8")
                .setSolverParameters(getResourceAsStream("/hvdc/solvers.par"), "2")
                .setSolverType(DynawoSimulationParameters.SolverType.IDA)
                .setSpecificLogs(EnumSet.allOf(DynawoSimulationParameters.SpecificLog.class));

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, EventModelsSupplier.empty(), OutputVariablesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, reportNode)
                .join();

        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getStatusText().isEmpty());
        assertTrue(result.getCurves().isEmpty());
        List<TimelineEvent> timeLine = result.getTimeLine();
        assertEquals(7, timeLine.size());
        checkTimeLineEvent(timeLine.getFirst(), 30.0, "_BUS____5-BUS____6-1_PS", "Tap position change (increment)");
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

        GroovyOutputVariablesSupplier outputVariablesSupplier = new GroovyOutputVariablesSupplier(
                getResourceAsStream("/smib/curves.groovy"),
                GroovyExtension.find(OutputVariableGroovyExtension.class, DynawoSimulationProvider.NAME));

        dynawoSimulationParameters.setModelsParameters(getResourceAsStream("/smib/SMIB.par"))
                .setNetworkParameters(getResourceAsStream("/smib/network.par"), "8")
                .setSolverParameters(getResourceAsStream("/smib/solvers.par"), "1")
                .setSolverType(DynawoSimulationParameters.SolverType.IDA);

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, outputVariablesSupplier,
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
        Network network = Network.read(new ResourceDataSource("powsybl_dynawo", new ResourceSet("/error", NETWORK_FILENAME)));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/error/models.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));
        GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
                getResourceAsStream("/error/eventModels.groovy"),
                GroovyExtension.find(EventModelGroovyExtension.class, DynawoSimulationProvider.NAME));
        GroovyOutputVariablesSupplier outputVariablesSupplier = new GroovyOutputVariablesSupplier(
                getResourceAsStream("/ieee14/disconnectline/outputVariables.groovy"),
                GroovyExtension.find(OutputVariableGroovyExtension.class, DynawoSimulationProvider.NAME));

        parameters.setStopTime(200);
        dynawoSimulationParameters.setModelsParameters(getResourceAsStream("/error/models.par"))
                .setNetworkParameters(getResourceAsStream("/error/network.par"), "NETWORK")
                .setSolverParameters(getResourceAsStream("/error/solvers.par"), "3")
                .setSolverType(DynawoSimulationParameters.SolverType.SIM);

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, outputVariablesSupplier,
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();

        assertEquals(DynamicSimulationResult.Status.FAILURE, result.getStatus());
        assertThat(result.getStatusText()).contains("time step <= 0.1 s for more than 10 iterations");
        assertThat(result.getTimeLine()).isNotEmpty();
        assertThat(result.getCurves()).isNotEmpty();
        assertThat(result.getFinalStateValues()).isNotEmpty();
    }

    @Test
    void testIeee14DynawoSuppliers() {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        DynamicModelsSupplier dynamicModelsSupplier = DynawoModelsSupplier.load(getResourceAsStream("/ieee14/disconnectline/dynamicModels.json"));
        EventModelsSupplier eventModelsSupplier = DynawoEventModelsSupplier.load(getResourceAsStream("/ieee14/disconnectline/eventModels.json"));

        dynawoSimulationParameters.setModelsParameters(getResourceAsStream("/ieee14/models.par"))
                .setNetworkParameters(getResourceAsStream("/ieee14/network.par"), "8")
                .setSolverParameters(getResourceAsStream("/ieee14/solvers.par"), "2")
                .setSolverType(DynawoSimulationParameters.SolverType.IDA)
                .setTimelineExportMode(ExportMode.XML);

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, OutputVariablesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();

        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
        assertTrue(result.getStatusText().isEmpty());
        assertEquals(0, result.getCurves().size());
        List<TimelineEvent> timeLine = result.getTimeLine();
        assertEquals(11, timeLine.size());
        checkTimeLineEvent(timeLine.getFirst(), 0, "_GEN____8_SM", "PMIN : activation");
    }

    @Test
    void testIeee14WithWrongGroovyVariablesForCRV() {
        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME,
                        PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("IEEE14 test")
                .build();

        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/ieee14/disconnectline/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
                getResourceAsStream("/ieee14/disconnectline/eventModels.groovy"),
                GroovyExtension.find(EventModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        GroovyOutputVariablesSupplier outputVariablesSupplier = new GroovyOutputVariablesSupplier(
                getResourceAsStream("/error/outputWrongVariables.groovy"),
                GroovyExtension.find(OutputVariableGroovyExtension.class, DynawoSimulationProvider.NAME));

        dynawoSimulationParameters.setModelsParameters(getResourceAsStream("/ieee14/models.par"))
                .setNetworkParameters(getResourceAsStream("/ieee14/network.par"), "8")
                .setSolverParameters(getResourceAsStream("/ieee14/solvers.par"), "2")
                .setSolverType(DynawoSimulationParameters.SolverType.IDA)
                .setTimelineExportMode(ExportMode.XML);

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, outputVariablesSupplier,
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, reportNode)
                .join();

        assertEquals(DynamicSimulationResult.Status.FAILURE, result.getStatus());
        assertEquals("CRV file is empty", result.getStatusText());
    }

    private void checkTimeLineEvent(TimelineEvent event, double time, String modelName, String message) {
        assertEquals(time, event.time());
        assertEquals(modelName, event.modelName());
        assertEquals(message, event.message());
    }

    @Test
    void testIEEE14SignalN() {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/ieee14/signal_n/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));
        GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
                getResourceAsStream("/ieee14/disconnectline/eventModels.groovy"),
                GroovyExtension.find(EventModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        dynawoSimulationParameters.setModelsParameters(getResourceAsStream("/ieee14/signal_n/IEEE14.par"))
                .setNetworkParameters(getResourceAsStream("/ieee14/signal_n/IEEE14.par"), "Network")
                .setSolverParameters(getResourceAsStream("/ieee14/signal_n/IEEE14.par"), "SimplifiedSolver")
                .setLogLevelFilter(DynawoSimulationParameters.LogLevel.DEBUG)
                .setSolverType(DynawoSimulationParameters.SolverType.SIM)
                .setTimelineExportMode(ExportMode.XML);

        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, OutputVariablesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, NO_OP)
                .join();

        assertEquals(DynamicSimulationResult.Status.SUCCESS, result.getStatus());
        assertThat(result.getStatusText()).isEmpty();
        assertThat(result.getCurves()).isEmpty();
        List<TimelineEvent> timeLine = result.getTimeLine();
        assertThat(timeLine).hasSize(13);
        checkTimeLineEvent(timeLine.get(12), 10, "_BUS____1-BUS____5-1_AC", "LINE : opening on side 2");
    }

    private Supplier<DynamicSimulationResult> setupIEEE14Simulation() {
        return setupIEEE14Simulation(NO_OP);
    }

    private Supplier<DynamicSimulationResult> setupIEEE14Simulation(ReportNode reportNode) {
        Network network = Network.read(new ResourceDataSource("IEEE14", new ResourceSet("/ieee14", "IEEE14.iidm")));

        GroovyDynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(
                getResourceAsStream("/ieee14/disconnectline/dynamicModels.groovy"),
                GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        GroovyEventModelsSupplier eventModelsSupplier = new GroovyEventModelsSupplier(
                getResourceAsStream("/ieee14/disconnectline/eventModels.groovy"),
                GroovyExtension.find(EventModelGroovyExtension.class, DynawoSimulationProvider.NAME));

        GroovyOutputVariablesSupplier outputVariablesSupplier = new GroovyOutputVariablesSupplier(
                getResourceAsStream("/ieee14/disconnectline/outputVariables.groovy"),
                GroovyExtension.find(OutputVariableGroovyExtension.class, DynawoSimulationProvider.NAME));

        dynawoSimulationParameters.setModelsParameters(getResourceAsStream("/ieee14/models.par"))
                .setNetworkParameters(getResourceAsStream("/ieee14/network.par"), "8")
                .setSolverParameters(getResourceAsStream("/ieee14/solvers.par"), "2")
                .setSolverType(DynawoSimulationParameters.SolverType.IDA)
                .setTimelineExportMode(ExportMode.XML);

        return () -> provider.run(network, dynamicModelsSupplier, eventModelsSupplier, outputVariablesSupplier,
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, reportNode)
                .join();
    }

    @Test
    void testDefaultModelEventConnections() {

        Network network = FourSubstationsNodeBreakerFactory.create();
        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME,
                        PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("test")
                .build();

        EventModelsSupplier eventModelsSupplier = (n, r) -> List.of(
                EventDisconnectionBuilder.of(n, r)
                        .staticId("GH1")
                        .startTime(10)
                        .build(),
                EventDisconnectionBuilder.of(n, r)
                        .staticId("HVDC1")
                        .startTime(20)
                        .build(),
                EventDisconnectionBuilder.of(n, r)
                        .staticId("LD5")
                        .startTime(30)
                        .build(),
                EventDisconnectionBuilder.of(n, r)
                        .staticId("SHUNT")
                        .startTime(40)
                        .build(),
                EventDisconnectionBuilder.of(n, r)
                        .staticId("SVC")
                        .startTime(50)
                        .build(),
                EventDisconnectionBuilder.of(n, r)
                        .staticId("TWT")
                        .startTime(60)
                        .build(),
                EventDisconnectionBuilder.of(n, r)
                        .staticId("LINE_S2S3")
                        .startTime(70)
                        .build(),
                EventActivePowerVariationBuilder.of(n, r)
                        .staticId("GH2")
                        .startTime(80)
                        .deltaP(0.5)
                        .build()
        );

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/ieee14/models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/ieee14/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/ieee14/solvers.par"), "2");
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.IDA);

        DynamicSimulationResult result = provider.run(network, (n, r) -> List.of(), eventModelsSupplier, OutputVariablesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, reportNode)
                .join();

        ReportNode eventReport = reportNode.getChildren().getFirst();
        assertEquals("dynawo.dynasim.dynawoSimulation", eventReport.getMessageKey());
        assertTrue(eventReport.getChildren().stream().allMatch(r -> r.getMessage().contains("instantiation OK")));
        assertEquals(DynamicSimulationResult.Status.FAILURE, result.getStatus());
        assertThat(result.getStatusText()).contains("KINSOL fails to solve the problem");

        // Test bus events
        network = EurostagTutorialExample1Factory.createWithLFResults();
        reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME,
                        PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("test")
                .build();

        eventModelsSupplier = (n, r) -> List.of(
                EventDisconnectionBuilder.of(n, r)
                        .staticId("NLOAD")
                        .startTime(5)
                        .build(),
                NodeFaultEventBuilder.of(n, r)
                        .staticId("GEN")
                        .startTime(10)
                        .faultTime(15)
                        .build()
        );
        result = provider.run(network, (n, r) -> List.of(), eventModelsSupplier, OutputVariablesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, reportNode)
                .join();

        eventReport = reportNode.getChildren().getFirst();
        assertEquals("dynawo.dynasim.dynawoSimulation", eventReport.getMessageKey());
        assertTrue(eventReport.getChildren().stream().allMatch(r -> r.getMessage().contains("instantiation OK")));
        assertEquals(DynamicSimulationResult.Status.FAILURE, result.getStatus());
        assertThat(result.getStatusText()).contains("KINSOL fails to solve the problem");
    }

    @Test
    void testDefaultModelConnections() {

        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME,
                        PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("test")
                .build();

        List<ParametersSet> modelsParameters = ParametersXml.load(getResourceAsStream("/automation_system_models.par"));
        ParametersSet networkParameters = ParametersXml.load(getResourceAsStream("/ieee14/network.par"), "8");
        ParametersSet solverParameters = ParametersXml.load(getResourceAsStream("/ieee14/solvers.par"), "2");
        dynawoSimulationParameters.setModelsParameters(modelsParameters)
                .setNetworkParameters(networkParameters)
                .setSolverParameters(solverParameters)
                .setSolverType(DynawoSimulationParameters.SolverType.IDA);

        DynamicModelsSupplier dynamicModelsSupplier = (n, r) -> List.of(
                DynamicOverloadManagementSystemBuilder.of(n, r)
                        .dynamicModelId("CLA_LINE")
                        .parameterSetId("CLA")
                        .iMeasurement(EurostagTutorialExample1Factory.NHV1_NHV2_2)
                        .iMeasurementSide(TwoSides.TWO)
                        .controlledBranch(EurostagTutorialExample1Factory.NHV1_NHV2_2)
                        .build(),
                DynamicOverloadManagementSystemBuilder.of(n, r)
                        .dynamicModelId("CLA_TFO")
                        .parameterSetId("CLA")
                        .iMeasurement(EurostagTutorialExample1Factory.NGEN_NHV1)
                        .iMeasurementSide(TwoSides.TWO)
                        .controlledBranch(EurostagTutorialExample1Factory.NGEN_NHV1)
                        .build(),
                PhaseShifterIAutomationSystemBuilder.of(n, r)
                        .dynamicModelId("PS")
                        .parameterSetId("PS")
                        .transformer(EurostagTutorialExample1Factory.NHV2_NLOAD)
                        .build(),
                TapChangerBlockingAutomationSystemBuilder.of(n, r)
                        .dynamicModelId("TCB")
                        .parameterSetId("TCB")
                        .uMeasurements(EurostagTutorialExample1Factory.NGEN)
                        .transformers(EurostagTutorialExample1Factory.NHV2_NLOAD)
                        .build()
        );
        EventModelsSupplier eventModelsSupplier = (n, r) -> List.of();
        DynamicSimulationResult result = provider.run(network, dynamicModelsSupplier, eventModelsSupplier, OutputVariablesSupplier.empty(),
                        VariantManagerConstants.INITIAL_VARIANT_ID, computationManager, parameters, reportNode)
                .join();

        ReportNode eventReport = reportNode.getChildren().getFirst();
        assertEquals("dynawo.dynasim.dynawoSimulation", eventReport.getMessageKey());
        assertTrue(eventReport.getChildren().stream().allMatch(r -> r.getMessage().contains("instantiation OK")));
        assertEquals(DynamicSimulationResult.Status.FAILURE, result.getStatus());
        assertThat(result.getStatusText()).contains("KINSOL fails to solve the problem");
    }
}
