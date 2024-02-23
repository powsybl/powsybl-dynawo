/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.dynawaltz.security.DynawoAlgorithmsConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class DynawoAlgorithmsConfigTest {

    private InMemoryPlatformConfig platformConfig;
    private FileSystem fileSystem;

    @BeforeEach
    void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void checkConfig() {
        String homeDir = "homeDir";
        boolean debug = true;

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawo-algorithms");
        moduleConfig.setStringProperty("homeDir", homeDir);
        moduleConfig.setStringProperty("debug", Boolean.toString(debug));
        DynawoAlgorithmsConfig config = DynawoAlgorithmsConfig.load(platformConfig);
        assertEquals(homeDir, config.getHomeDir().toString());
        assertEquals(debug, config.isDebug());
    }

}
