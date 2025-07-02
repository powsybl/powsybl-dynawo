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

import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian {@literal <geoffroy.jamgotchian at rte-france.com>}
 */
public abstract class AbstractDynawoTest {

    private static final String DOCKER_IMAGE_ID = "powsybl/java-dynawo:2.2.0";

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

    protected static String getResource(String resourceName) {
        return Objects.requireNonNull(AbstractDynawoTest.class.getResource(resourceName)).getPath();
    }

    protected static Path getResourcePath(String resourceName) {
        try {
            return Path.of(Objects.requireNonNull(AbstractDynawoTest.class.getResource(resourceName)).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
