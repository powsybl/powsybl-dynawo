package com.powsybl.dynaflow;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Paths;

import static com.powsybl.dynaflow.DynaflowConstants.CONFIG_FILENAME;
import static com.powsybl.dynaflow.DynaflowConstants.IIDM_IN_FILE;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;

public class DynaflowConfigTest {
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

    @Test
    public void checkConfig() {
        String homeDir = "homeDir";
        boolean debug = true;

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynaflow");
        moduleConfig.setStringProperty("homeDir", homeDir);
        moduleConfig.setStringProperty("debug", Boolean.toString(debug));
        DynaflowConfig config = DynaflowConfig.fromPlatformConfig(platformConfig);
        assertEquals(homeDir, config.getHomeDir().toString());
        assertEquals(debug, config.isDebug());
    }

    @Test
    public void checkVersionCommand() {
        String homeDir = "homeDir";
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynaflow");
        moduleConfig.setStringProperty("homeDir", homeDir);
        DynaflowConfig config = DynaflowConfig.fromPlatformConfig(platformConfig);
        String versionCommand = config.getVersionCommand().toString(0);
        String expectedVersionCommand = "[homeDir" + File.separator + "dynaflow-launcher.sh, --version]";
        assertEquals(expectedVersionCommand, versionCommand);
    }

    @Test
    public void checkExecutionCommand() {
        String homeDir = "homeDir";
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynaflow");
        moduleConfig.setStringProperty("homeDir", homeDir);
        DynaflowConfig config = DynaflowConfig.fromPlatformConfig(platformConfig);
        String workingDir = File.separator + "tmp" + File.separator + "dynaflow_123";
        String executionCommand = config.getCommand(Paths.get(workingDir)).toString(0);
        String expectedExecutionCommand = "[homeDir" + File.separator + "dynaflow-launcher.sh, --iidm, " + workingDir
                + File.separator + IIDM_IN_FILE + ", --config, " + workingDir + File.separator + CONFIG_FILENAME + "]";
        assertEquals(expectedExecutionCommand, executionCommand);
    }
}
