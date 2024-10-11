/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.serde.NetworkSerDe;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

import static com.powsybl.commons.test.ComparisonUtils.assertXmlEquals;
import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static com.powsybl.dynawo.commons.DynawoConstants.*;
import static com.powsybl.loadflow.LoadFlowResult.Status.FAILED;
import static com.powsybl.loadflow.LoadFlowResult.Status.FULLY_CONVERGED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 */
class DynaFlowProviderTest extends AbstractSerDeTest {

    private Path homeDir;
    private DynaFlowConfig config;
    private DynaFlowProvider provider;

    @BeforeEach
    @Override
    public void setUp() throws IOException {
        super.setUp();
        homeDir = fileSystem.getPath("/home/dynaflow");
        config = DynaFlowConfig.load();
        provider = new DynaFlowProvider();
    }

    @Test
    void checkVersionCommand() {
        String versionCommand = DynaFlowProvider.getVersionCommand(config).toString(0);
        String expectedVersionCommand = "[" + getProgram(homeDir) + ", --version]";
        assertEquals(expectedVersionCommand, versionCommand);
    }

    @Test
    void checkExecutionCommand() {
        String executionCommand = DynaFlowProvider.getCommand(config).toString(0);
        String expectedExecutionCommand = "[" + getProgram(homeDir) + ", --network, " + NETWORK_FILENAME + ", --config, " + CONFIG_FILENAME + "]";
        assertEquals(expectedExecutionCommand, executionCommand);
    }

    private static String getProgram(Path homeDir) {
        return homeDir.resolve(SystemUtils.IS_OS_WINDOWS ? "dynaflow-launcher.cmd" : "dynaflow-launcher.sh").toString();
    }

    private static class LocalCommandExecutorMock extends AbstractLocalCommandExecutor {

        private final String stdOutFileRef;
        private final String outputIidm;
        private final String outputResults;

        public LocalCommandExecutorMock(String stdoutFileRef, String outputIidm, String outputResults) {
            this.stdOutFileRef = Objects.requireNonNull(stdoutFileRef);
            this.outputIidm = Objects.requireNonNull(outputIidm);
            this.outputResults = Objects.requireNonNull(outputResults);
        }

        @Override
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
            try {
                copyFile(stdOutFileRef, errFile);
                copyFile(outputResults, workingDir.resolve(OUTPUT_RESULTS_FILENAME));
                Path finalState = Files.createDirectories(workingDir.resolve(OUTPUTS_FOLDER).resolve(FINAL_STATE_FOLDER));
                copyFile(outputIidm, finalState.resolve(OUTPUT_IIDM_FILENAME));

                return 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static class EmptyLocalCommandExecutorMock extends AbstractLocalCommandExecutor {

        private final String stdOutFileRef;

        public EmptyLocalCommandExecutorMock(String stdoutFileRef) {
            this.stdOutFileRef = Objects.requireNonNull(stdoutFileRef);
        }

        @Override
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
            try {
                copyFile(stdOutFileRef, errFile);
                Files.createDirectories(workingDir.resolve(OUTPUTS_FOLDER).resolve(FINAL_STATE_FOLDER));
                return 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Test
    void testWithoutMergeLoads() throws Exception {
        Network network = createTestNetwork();
        LoadFlow.Runner dynaFlowSimulation = LoadFlow.find();
        LoadFlowParameters params = LoadFlowParameters.load();
        DynaFlowParameters dynaFlowParameters = params.getExtension(DynaFlowParameters.class);
        dynaFlowParameters.setMergeLoads(false);

        assertEquals(DYNAFLOW_NAME, dynaFlowSimulation.getName());

        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynawo_version.out",
                "/output.xiidm", "/results.json");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        LoadFlowResult result = dynaFlowSimulation.run(network, computationManager, params);
        assertNotNull(result);
        assertEquals(FULLY_CONVERGED, result.getStatus());

        InputStream pReferenceOutput = getClass().getResourceAsStream("/output.xiidm");
        Network expectedNetwork = NetworkSerDe.read(pReferenceOutput);

        compare(expectedNetwork, network);
    }

    @Test
    void testWithMergeLoads() throws Exception {
        Network network = createTestNetwork();
        LoadFlow.Runner dynaFlowSimulation = LoadFlow.find();
        LoadFlowParameters params = LoadFlowParameters.load();

        assertEquals(DYNAFLOW_NAME, dynaFlowSimulation.getName());

        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynawo_version.out",
                "/outputMergedLoads.xiidm", "/results.json");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        LoadFlowResult result = dynaFlowSimulation.run(network, computationManager, params);
        assertNotNull(result);
        assertEquals(FULLY_CONVERGED, result.getStatus());

        InputStream pReferenceOutput = getClass().getResourceAsStream("/output.xiidm");
        Network expectedNetwork = NetworkSerDe.read(pReferenceOutput);

        compare(expectedNetwork, network);
    }

    @Test
    void testFail() throws Exception {
        Network network = Network.create("empty", "test");
        LoadFlow.Runner dynaFlowSimulation = LoadFlow.find();
        LoadFlowParameters params = LoadFlowParameters.load();

        assertEquals(DYNAFLOW_NAME, dynaFlowSimulation.getName());

        LocalCommandExecutor commandExecutor = new EmptyLocalCommandExecutorMock("/dynawo_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        LoadFlowResult result = dynaFlowSimulation.run(network, computationManager, params);
        assertNotNull(result);
        assertEquals(FAILED, result.getStatus());
    }

    @Test
    void testCallingBadVersionDynaFlow() throws Exception {
        Network network = Network.create("empty", "test");
        LoadFlow.Runner dynaFlowSimulation = LoadFlow.find();
        LoadFlowParameters params = LoadFlowParameters.load();

        LocalCommandExecutor commandExecutor = new EmptyLocalCommandExecutorMock("/dynawo_bad_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        PowsyblException e = assertThrows(PowsyblException.class, () -> dynaFlowSimulation.run(network, computationManager, params));
        assertEquals("dynaflow-launcher version not supported. Must be >= " + DynawoConstants.VERSION_MIN, e.getMessage());
    }

    @Test
    void testUpdateSpecificParameters() {
        Map<String, String> properties = Map.of(
                "svcRegulationOn", "true",
                "shuntRegulationOn", "true",
                "automaticSlackBusOn", "false",
                "dsoVoltageLevel", "2.0",
                "chosenOutputs", "STEADYSTATE, CONSTRAINTS",
                "timeStep", "0");

        LoadFlowParameters params = LoadFlowParameters.load();
        DynaFlowParameters dynaParams = params.getExtension(DynaFlowParameters.class);
        provider.updateSpecificParameters(dynaParams, properties);

        assertTrue(dynaParams.getSvcRegulationOn());
        assertTrue(dynaParams.getShuntRegulationOn());
        assertFalse(dynaParams.getAutomaticSlackBusOn());
        assertEquals(2, dynaParams.getDsoVoltageLevel(), 0.1d);
        assertThat(dynaParams.getChosenOutputs()).containsExactlyInAnyOrder(OutputTypes.STEADYSTATE, OutputTypes.CONSTRAINTS);
        assertEquals(0, dynaParams.getTimeStep(), 0.1d);
    }

    @Test
    void testGetSpecificParameters() {
        Map<String, String> expectedProperties = Map.ofEntries(
                Map.entry("svcRegulationOn", "true"),
                Map.entry("dsoVoltageLevel", "45.0"),
                Map.entry("shuntRegulationOn", "true"),
                Map.entry("automaticSlackBusOn", "true"),
                Map.entry("timeStep", "10.0"),
                Map.entry("startingPointMode", "WARM"),
                Map.entry("startTime", "0.0"),
                Map.entry("stopTime", "100.0"),
                Map.entry("activePowerCompensation", "PMAX"),
                Map.entry("chosenOutputs", "TIMELINE"),
                Map.entry("mergeLoads", "true"));

        LoadFlowParameters params = LoadFlowParameters.load();
        DynaFlowParameters dynaParams = params.getExtension(DynaFlowParameters.class);
        Map<String, String> properties = provider.createMapFromSpecificParameters(dynaParams);
        assertThat(properties).containsExactlyInAnyOrderEntriesOf(expectedProperties);
    }

    private void compare(Network expected, Network actual) throws IOException {
        Path pexpected = tmpDir.resolve("expected.xiidm");
        assertNotNull(pexpected);
        Path pactual = tmpDir.resolve("actual.xiidm");
        assertNotNull(pactual);
        NetworkSerDe.write(expected, pexpected);
        actual.setCaseDate(expected.getCaseDate());
        NetworkSerDe.write(actual, pactual);
        assertXmlEquals(Files.newInputStream(pexpected), Files.newInputStream(pactual));
    }

    private static Network createTestNetwork() {
        Network network = Network.create("test", "test");
        Substation s = network.newSubstation().setId("substation").add();

        VoltageLevel vl1 = s.newVoltageLevel().setId("vl1").setNominalV(400).setTopologyKind(TopologyKind.NODE_BREAKER).add();
        vl1.getNodeBreakerView().newBusbarSection().setId("Busbar").setNode(0).add();
        vl1.getNodeBreakerView().newDisconnector().setNode1(0).setNode2(1).setId("d1").add();
        vl1.getNodeBreakerView().newDisconnector().setNode1(0).setNode2(2).setId("d2").add();
        vl1.getNodeBreakerView().newDisconnector().setNode1(0).setNode2(3).setId("d3").add();
        vl1.newLoad().setId("load1").setP0(10.0).setQ0(5.0).setNode(1).add();
        vl1.newLoad().setId("load2").setP0(12.0).setQ0(1.0).setNode(2).add();

        VoltageLevel vl2 = s.newVoltageLevel().setId("vl2").setNominalV(400).setTopologyKind(TopologyKind.BUS_BREAKER).add();
        Bus b1 = vl2.getBusBreakerView().newBus().setId("b1").add();
        vl2.getBusBreakerView().newBus().setId("b2").add();
        vl2.getBusBreakerView().newSwitch().setId("c").setBus1("b1").setBus2("b2").add();
        vl2.newGenerator().setId("g1").setBus("b1").setTargetP(101).setTargetV(390).setMinP(0).setMaxP(150).setVoltageRegulatorOn(true).add();
        vl2.newLoad().setId("load3").setP0(77.0).setQ0(1.0).setBus("b2").add();

        network.newLine().setId("l1").setVoltageLevel1(vl1.getId()).setNode1(3).setVoltageLevel2(vl2.getId()).setBus2(b1.getId())
                .setR(1).setX(3).setG1(0).setG2(0).setB1(0).setB2(0).add();
        return network;
    }
}
