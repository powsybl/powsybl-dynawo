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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaflowConfigTest {
    private FileSystem fileSystem;
    private final boolean debug = true;
    private final String homeDir = "homeDir";

    @Before
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.windows());
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void fromPlatformConfigTest() {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynaflow");
        moduleConfig.setStringProperty("homeDir", homeDir);
        moduleConfig.setStringProperty("debug", Boolean.toString(debug));

        DynaflowConfig config = DynaflowConfig.fromPlatformConfig(platformConfig);
        assertEquals(homeDir, config.getHomeDir().toString());
        assertEquals(debug, config.isDebug());
    }

    @Test
    public void checkGetters() {
        Path pathHomeDir = fileSystem.getPath(homeDir);
        DynaflowConfig config = new DynaflowConfig(pathHomeDir, debug);

        assertEquals(homeDir, config.getHomeDir().toString());
        assertEquals(debug, config.isDebug());
    }
}
