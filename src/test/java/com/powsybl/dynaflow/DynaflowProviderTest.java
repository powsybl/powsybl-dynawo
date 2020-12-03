/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
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

import static com.powsybl.dynaflow.DynaflowConstants.CONFIG_FILENAME;
import static com.powsybl.dynaflow.DynaflowConstants.IIDM_FILENAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaflowProviderTest {
    private InMemoryPlatformConfig platformConfig;
    private FileSystem fileSystem;
    private String homeDir;
    private DynaflowProvider provider;

    @Before
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
        homeDir = "/home/dynaflow";
        provider = new DynaflowProvider();
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
        Path workingDir = fileSystem.getPath("tmp").resolve("dynaflow");
        String program = fileSystem.getPath(homeDir).resolve("dynaflow-launcher.sh").toString();
        String iidmPath = workingDir.resolve(IIDM_FILENAME).toString();
        String configPath = workingDir.resolve(CONFIG_FILENAME).toString();

        String executionCommand = provider.getCommand(workingDir).toString(0);
        String expectedExecutionCommand = "[" + program + ", --iidm, " + iidmPath + ", --config, " + configPath + "]";
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

        LoadFlow.Runner dynaflowSimulation = LoadFlow.find();
        LoadFlowParameters params = LoadFlowParameters.load();

        assertEquals("DynaFlow", dynaflowSimulation.getName());
        assertEquals("0.1", dynaflowSimulation.getVersion());

        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynaflow/dynaflow_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        LoadFlowResult result = dynaflowSimulation.run(network, computationManager, params);
        assertNotNull(result);
    }
}
