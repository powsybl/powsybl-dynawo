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
import com.powsybl.dynawo.commons.DynawoUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
class DynawoVersionCheckTest {

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
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
            try {
                copyFile(stdOutFileRef, errFile);
                return 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @BeforeEach
    void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void versionTest() throws IOException {
        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynawo_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        assertTrue(DynawoUtil.checkDynawoVersion(env, computationManager, versionCmd, true));
    }

    @Test
    void badVersionTest() throws IOException {
        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynawo_bad_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        assertFalse(DynawoUtil.checkDynawoVersion(env, computationManager, versionCmd, true));
    }

    @Test
    void versionTestNotExistingFile() throws IOException {
        Command badVersionCmd = new SimpleCommandBuilder()
                .id("does_not_exist")
                .program("dummy")
                .build();
        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynaflow_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        CompletionException e = assertThrows(CompletionException.class, () -> DynawoUtil.checkDynawoVersion(env, computationManager, badVersionCmd, true));
        assertEquals("com.powsybl.commons.PowsyblException: No output for DynaFlow version command", e.getMessage());
    }
}
