/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.it;

import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.test.ComputationDockerConfig;
import com.powsybl.computation.local.test.DockerLocalComputationManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Geoffroy Jamgotchian {@literal <geoffroy.jamgotchian at rte-france.com>}
 */
public abstract class AbstractDynawoTest {

    private static final String DOCKER_IMAGE_ID = "powsybl/java-dynawo:3.1.0";

    @TempDir
    Path localDir;

    protected ComputationManager computationManager;

    @BeforeEach
    void setUp() throws Exception {
        Path dockerDir = Path.of("/home/powsybl");
        ComputationDockerConfig config = new ComputationDockerConfig()
                .setDockerImageId(DOCKER_IMAGE_ID);
        computationManager = new DockerLocalComputationManager(localDir, dockerDir, config);
    }

    @AfterEach
    void tearDown() {
        computationManager.close();
    }

    protected static InputStream getResourceAsStream(String name) {
        return Objects.requireNonNull(AbstractDynawoTest.class.getResourceAsStream(name));
    }

    protected void testExecutionTempFile() {
        Path execTmpDir = localDir.getParent();
        Path execTmpFilePath = execTmpDir.resolve(".EXEC_TMP_FILENAME");
        try {
            String content = Files.readString(execTmpFilePath);
            Path referencedFile = Paths.get(content.trim());

            assertTrue(Files.exists(execTmpFilePath));
            assertNotNull(content);
            assertFalse(content.isBlank());
            assertTrue(Files.exists(referencedFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
