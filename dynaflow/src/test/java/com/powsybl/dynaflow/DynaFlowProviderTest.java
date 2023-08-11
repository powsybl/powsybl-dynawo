/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.test.AbstractConverterTest;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.xml.NetworkXml;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

import static com.powsybl.commons.test.ComparisonUtils.compareXml;
import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static com.powsybl.dynawo.commons.DynawoConstants.OUTPUT_IIDM_FILENAME;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
class DynaFlowProviderTest extends AbstractConverterTest {

    private Path homeDir;
    private DynaFlowConfig config;
    private DynaFlowProvider provider;

    @BeforeEach
    public void setUp() throws IOException {
        super.setUp();
        homeDir = fileSystem.getPath("/home/dynaflow");
        config = DynaFlowConfig.fromPropertyFile();
        provider = new DynaFlowProvider();
    }

    @Test
    void checkVersionCommand() {
        String program = homeDir.resolve("dynaflow-launcher.sh").toString();
        String versionCommand = DynaFlowProvider.getVersionCommand(config).toString(0);
        String expectedVersionCommand = "[" + program + ", --version]";
        assertEquals(expectedVersionCommand, versionCommand);
    }

    @Test
    void checkExecutionCommand() {
        String program = homeDir.resolve("dynaflow-launcher.sh").toString();
        String executionCommand = DynaFlowProvider.getCommand(config).toString(0);
        String expectedExecutionCommand = "[" + program + ", --network, " + IIDM_FILENAME + ", --config, " + CONFIG_FILENAME + "]";
        assertEquals(expectedExecutionCommand, executionCommand);
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
                Files.createDirectories(workingDir.resolve("outputs").resolve("finalState"));
                copyFile(outputIidm, workingDir.resolve("outputs").resolve("finalState").resolve(OUTPUT_IIDM_FILENAME));

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
                Files.createDirectories(workingDir.resolve("outputs").resolve("finalState"));
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
        assertTrue(result.isOk());

        InputStream pReferenceOutput = getClass().getResourceAsStream("/output.xiidm");
        Network expectedNetwork = NetworkXml.read(pReferenceOutput);

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
        assertTrue(result.isOk());

        InputStream pReferenceOutput = getClass().getResourceAsStream("/output.xiidm");
        Network expectedNetwork = NetworkXml.read(pReferenceOutput);

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
        assertFalse(result.isOk());
    }

    @Test
    void testCallingBadVersionDynaFlow() throws Exception {
        Network network = Network.create("empty", "test");
        LoadFlow.Runner dynaFlowSimulation = LoadFlow.find();
        LoadFlowParameters params = LoadFlowParameters.load();

        LocalCommandExecutor commandExecutor = new EmptyLocalCommandExecutorMock("/dynawo_bad_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        PowsyblException e = assertThrows(PowsyblException.class, () -> dynaFlowSimulation.run(network, computationManager, params));
        assertEquals("DynaFlow version not supported. Must be >= 1.3.0", e.getMessage());
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
        assertArrayEquals(Arrays.asList(OutputTypes.STEADYSTATE.name(), OutputTypes.CONSTRAINTS.name()).toArray(), dynaParams.getChosenOutputs().toArray());
        assertEquals(0, dynaParams.getTimeStep(), 0.1d);
    }

    private void compare(Network expected, Network actual) throws IOException {
        Path pexpected = tmpDir.resolve("expected.xiidm");
        assertNotNull(pexpected);
        Path pactual = tmpDir.resolve("actual.xiidm");
        assertNotNull(pactual);
        NetworkXml.write(expected, pexpected);
        actual.setCaseDate(expected.getCaseDate());
        NetworkXml.write(actual, pactual);
        compareXml(Files.newInputStream(pexpected), Files.newInputStream(pactual));
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
