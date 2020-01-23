package com.powsybl.dynawo.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.file.FileSystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;

public class DynawoConfigTest {

    private InMemoryPlatformConfig platformConfig;
    private FileSystem fileSystem;

    @Before
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    private void checkValues(DynawoConfig config, String homeDir, boolean debug) {
        assertEquals(config.getHomeDir(), homeDir);
        assertEquals(config.isDebug(), debug);
    }

    @Test
    public void testNoConfig() {
        DynawoConfig config = DynawoConfig.load(platformConfig);
        assertNull(config);
    }

    @Test
    public void checkConfig() throws IOException {
        String homeDir = "homeDir";
        boolean debug = true;

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawo");
        moduleConfig.setStringProperty("homeDir", homeDir);
        moduleConfig.setStringProperty("debug", Boolean.toString(debug));
        DynawoConfig config = DynawoConfig.load(platformConfig);
        checkValues(config, homeDir, debug);
    }

}
