/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

import static com.powsybl.dynaflow.DynaFlowConstants.CONFIG_FILENAME;
import static com.powsybl.dynaflow.DynaFlowConstants.IIDM_FILENAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaFlowProviderTest {
    private FileSystem fileSystem;
    private String homeDir;
    private DynaFlowProvider provider;

    @Before
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        homeDir = "/home/dynaflow";
        provider = new DynaFlowProvider();
    }

    @Test
    public void checkVersionCommand() {
        Path pathHomeDir = fileSystem.getPath(homeDir);
        String program = pathHomeDir.resolve("dynaflow-launcher.sh").toString();

        String versionCommand = provider.getVersionCommand().toString(0);
        String expectedVersionCommand = "[" + program + ", --version]";

        assertEquals(expectedVersionCommand, versionCommand);
    }

    @Test
    public void checkExecutionCommand() {
        String program = fileSystem.getPath(homeDir).resolve("dynaflow-launcher.sh").toString();

        String executionCommand = provider.getCommand().toString(0);
        String expectedExecutionCommand = "[" + program + ", --iidm, " + IIDM_FILENAME + ", --config, " + CONFIG_FILENAME + "]";
        assertEquals(expectedExecutionCommand, executionCommand);
    }

    private static class LocalCommandExecutorMock extends AbstractLocalCommandExecutor {

        private final String stdOutFileRef;

        public LocalCommandExecutorMock(String stdoutFileRef) {
            this.stdOutFileRef = Objects.requireNonNull(stdoutFileRef);
        }

        @Override
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
            try {
                copyFile(stdOutFileRef, errFile);

                return 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Test
    public void test() throws Exception {
        Network network = Network.create("test", "test");

        LoadFlow.Runner dynaFlowSimulation = LoadFlow.find();
        LoadFlowParameters params = LoadFlowParameters.load();

        assertEquals("DynaFlow", dynaFlowSimulation.getName());
        assertEquals("0.1", dynaFlowSimulation.getVersion());

        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynaflow_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        LoadFlowResult result = dynaFlowSimulation.run(network, computationManager, params);
        assertNotNull(result);
    }
}