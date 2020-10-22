/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.computation.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaflowVersionCheckTest {

    private FileSystem fileSystem;
    private final ExecutionEnvironment env = Mockito.mock(ExecutionEnvironment.class);
    private final Command versionCmd = new SimpleCommandBuilder()
            .id("dynaflow_version")
            .program("dummy")
            .build();

    private class MockComputationManager implements ComputationManager {

        private final String stdOutFileRef;

        MockComputationManager(String stdOutFileRef) {
            this.stdOutFileRef = stdOutFileRef;
        }

        @Override
        public String getVersion() {
            return null;
        }

        @Override
        public OutputStream newCommonFile(String fileName) throws IOException {
            return null;
        }

        @Override
        public <R> CompletableFuture<R> execute(ExecutionEnvironment environment, ExecutionHandler<R> executionHandler) {
            return CompletableFutureTask.runAsync(() -> {
                try {
                    Path path = fileSystem.getPath("/working-dir");
                    Files.createDirectory(path);

                    copyFile(stdOutFileRef, path.resolve("dynaflow_version_0.err"));

                    return executionHandler.after(path, new DefaultExecutionReport(path));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }, ForkJoinPool.commonPool());
        }

        @Override
        public ComputationResourcesStatus getResourcesStatus() {
            return null;
        }

        @Override
        public Executor getExecutor() {
            return null;
        }

        @Override
        public Path getLocalDir() {
            return null;
        }

        @Override
        public void close() {

        }

        private void copyFile(String source, Path target) throws IOException {
            try (InputStream is = getClass().getResourceAsStream(source)) {
                Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
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
    public void versionTest() {
        assertTrue(DynaflowUtil.checkDynaflowVersion(env, new MockComputationManager("/dynaflow/dynaflow_version.out"), versionCmd));
    }

    @Test(expected = java.util.concurrent.CompletionException.class)
    public void versionTestNotExistingFile() {
        Command badVersionCmd = new SimpleCommandBuilder()
                .id("does_not_exist")
                .program("dummy")
                .build();
        DynaflowUtil.checkDynaflowVersion(env, new MockComputationManager("/dynaflow/dynaflow_version.out"), badVersionCmd);
    }
}
