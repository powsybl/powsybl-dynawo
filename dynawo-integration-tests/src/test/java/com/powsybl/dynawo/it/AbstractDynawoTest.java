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
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public abstract class AbstractDynawoTest {

    private static final String DYNAWO_VERSION = "1.4.0";

    private static final String DOCKER_IMAGE_ID = "powsybl/java-dynawo:" + DYNAWO_VERSION;

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    protected Path localDir;

    protected ComputationManager computationManager;

    @Before
    public void setUp() throws Exception {
        localDir = tempFolder.getRoot().toPath();
        Path dockerDir = Path.of("/home/powsybl");
        ComputationDockerConfig config = new ComputationDockerConfig()
                .setDockerImageId(DOCKER_IMAGE_ID);
        computationManager = new DockerLocalComputationManager(localDir, dockerDir, config);
    }

    @After
    public void tearDown() {
        computationManager.close();
    }

    protected static InputStream getResourceAsStream(String name) {
        return Objects.requireNonNull(AbstractDynawoTest.class.getResourceAsStream(name));
    }
}
