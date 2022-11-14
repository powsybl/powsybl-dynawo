/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.NetworkXml;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import org.junit.Before;
import org.junit.Test;

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

import static com.powsybl.commons.ComparisonUtils.compareXml;
import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static org.junit.Assert.*;

/**
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaFlowProviderTest extends AbstractConverterTest {

    private String homeDir;
    private DynaFlowConfig config;
    private DynaFlowProvider provider;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        homeDir = "/home/dynaflow";
        config = DynaFlowConfig.fromPropertyFile();
        provider = new DynaFlowProvider();
    }

    @Test
    public void checkVersionCommand() {
        Path pathHomeDir = fileSystem.getPath(homeDir);
        String program = pathHomeDir.resolve("dynaflow-launcher.sh").toString();

        String versionCommand = DynaFlowProvider.getVersionCommand(config).toString(0);
        String expectedVersionCommand = "[" + program + ", --version]";

        assertEquals(expectedVersionCommand, versionCommand);
    }

    @Test
    public void checkExecutionCommand() {
        String program = fileSystem.getPath(homeDir).resolve("dynaflow-launcher.sh").toString();

        String executionCommand = provider.getCommand(config).toString(0);
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
    public void test() throws Exception {
        Network network = createTestSmallBusBranch();
        LoadFlow.Runner dynaFlowSimulation = LoadFlow.find();
        LoadFlowParameters params = LoadFlowParameters.load();

        assertEquals("DynaFlow", dynaFlowSimulation.getName());
        assertEquals("0.1", dynaFlowSimulation.getVersion());

        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynaflow_version.out",
                "/SmallBusBranch_outputIIDM.xml", "/results.json");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        LoadFlowResult result = dynaFlowSimulation.run(network, computationManager, params);
        assertNotNull(result);
        assertTrue(result.isOk());
    }

    @Test
    public void testFail() throws Exception {
        Network network = createTestSmallBusBranch();
        LoadFlow.Runner dynaFlowSimulation = LoadFlow.find();
        LoadFlowParameters params = LoadFlowParameters.load();

        assertEquals("DynaFlow", dynaFlowSimulation.getName());
        assertEquals("0.1", dynaFlowSimulation.getVersion());

        LocalCommandExecutor commandExecutor = new EmptyLocalCommandExecutorMock("/dynaflow_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        LoadFlowResult result = dynaFlowSimulation.run(network, computationManager, params);
        assertNotNull(result);
        assertFalse(result.isOk());
    }

    @Test
    public void testUpdate() throws Exception {
        Network network = createTestSmallBusBranch();
        LoadFlow.Runner dynaFlowSimulation = LoadFlow.find();
        LoadFlowParameters params = LoadFlowParameters.load();

        assertEquals("DynaFlow", dynaFlowSimulation.getName());
        assertEquals("0.1", dynaFlowSimulation.getVersion());

        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynaflow_version.out",
                "/SmallBusBranch_outputIIDM.xml", "/results.json");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        LoadFlowResult result = dynaFlowSimulation.run(network, computationManager, params);
        assertNotNull(result);
        assertTrue(result.isOk());

        InputStream pReferenceOutput = getClass().getResourceAsStream("/SmallBusBranch_outputIIDM.xml");
        Network expectedNetwork = NetworkXml.read(pReferenceOutput);

        compare(expectedNetwork, network);
    }

    @Test
    public void testUpdateSpecificParameters() {
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

    private static Network createTestSmallBusBranch() {
        return Importers.importData("XIIDM", new ResourceDataSource("SmallBusBranch", new ResourceSet("/", "SmallBusBranch.xiidm")), null);
    }

}
