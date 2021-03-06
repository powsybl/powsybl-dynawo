/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.computation.Command;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.ExecutionEnvironment;
import com.powsybl.computation.SimpleCommandBuilder;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaFlowVersionCheckTest {

    private FileSystem fileSystem;
    private final ExecutionEnvironment env = Mockito.mock(ExecutionEnvironment.class);
    private final Command versionCmd = new SimpleCommandBuilder()
            .id("dynaflow_version")
            .program("dummy")
            .build();

    private static class LocalCommandExecutorMock extends AbstractLocalCommandExecutor {

        private final String stdOutFileRef;

        public LocalCommandExecutorMock(String stdoutFileRef) {
            this.stdOutFileRef = Objects.requireNonNull(stdoutFileRef);
        }

        @Override
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) throws IOException, InterruptedException {
            try {
                copyFile(stdOutFileRef, workingDir.resolve("dynaflow_version_0.err"));

                return 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Before
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void versionTest() throws IOException {
        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynaflow_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        assertTrue(DynaFlowUtil.checkDynaFlowVersion(env, computationManager, versionCmd));
    }

    @Test
    public void badVersionTest() throws IOException {
        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynaflow_bad_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        assertFalse(DynaFlowUtil.checkDynaFlowVersion(env, computationManager, versionCmd));
    }

    @Test(expected = java.util.concurrent.CompletionException.class)
    public void versionTestNotExistingFile() throws IOException {
        Command badVersionCmd = new SimpleCommandBuilder()
                .id("does_not_exist")
                .program("dummy")
                .build();
        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynaflow_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        DynaFlowUtil.checkDynaFlowVersion(env, computationManager, badVersionCmd);
    }
}
