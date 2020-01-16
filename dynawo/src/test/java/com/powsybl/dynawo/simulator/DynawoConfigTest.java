package com.powsybl.dynawo.simulator;

import static org.junit.Assert.assertEquals;

import java.nio.file.FileSystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;

public class DynawoConfigTest {

    InMemoryPlatformConfig platformConfig;
    FileSystem fileSystem;

    @Before
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
    }

    @After
    public void tearDown() throws Exception {
        fileSystem.close();
    }

    private void checkValues(DynawoConfig config, String homeDir, boolean debug) {
        assertEquals(config.getHomeDir(), homeDir);
        assertEquals(config.isDebug(), debug);
    }

    @Test
    public void testNoConfig() {
        DynawoConfig config = new DynawoConfig();
        DynawoConfig.load(config, platformConfig);
        checkValues(config, DynawoConfig.HOME_DIR, DynawoConfig.DEBUG_MODE);
    }

    @Test
    public void checkConfig() throws Exception {
        String homeDir = "homeDir";
        boolean debug = true;

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawo");
        moduleConfig.setStringProperty("homeDir", homeDir);
        moduleConfig.setStringProperty("debug", Boolean.toString(debug));
        DynawoConfig config = new DynawoConfig();
        DynawoConfig.load(config, platformConfig);
        checkValues(config, homeDir, debug);
    }

}
