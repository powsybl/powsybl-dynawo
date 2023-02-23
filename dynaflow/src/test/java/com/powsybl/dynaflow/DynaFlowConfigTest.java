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
import com.powsybl.commons.config.MapModuleConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
class DynaFlowConfigTest {

    private FileSystem fileSystem;
    private final boolean debug = true;
    private final String homeDir = "homeDir";

    @BeforeEach
    void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.windows());
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void fromPlatformConfigTest() {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynaflow");
        moduleConfig.setStringProperty("homeDir", homeDir);
        moduleConfig.setStringProperty("debug", Boolean.toString(debug));

        DynaFlowConfig config = DynaFlowConfig.fromPlatformConfig(platformConfig);
        assertEquals(homeDir, config.getHomeDir().toString());
        assertEquals(debug, config.isDebug());
    }

    @Test
    void fromPlatformConfigNull() {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);

        DynaFlowConfig config = DynaFlowConfig.fromPlatformConfig(platformConfig);
        assertNull(config);
    }

    @Test
    void checkGetters() {
        Path pathHomeDir = fileSystem.getPath(homeDir);
        DynaFlowConfig config = new DynaFlowConfig(pathHomeDir, debug);

        assertEquals(homeDir, config.getHomeDir().toString());
        assertEquals(debug, config.isDebug());
    }
}
