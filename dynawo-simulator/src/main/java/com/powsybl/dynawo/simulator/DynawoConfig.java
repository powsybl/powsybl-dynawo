package com.powsybl.dynawo.simulator;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;

public class DynawoConfig {

    // default eustag_cpt command name
    private static final String DEFAULT_DYNAWO_CMD_NAME = "myEnvDynawo.sh";

    public static synchronized DynawoConfig load() {
        return load(PlatformConfig.defaultConfig());
    }

    public static synchronized DynawoConfig load(PlatformConfig platformConfig) {
        ModuleConfig config = platformConfig.getModuleConfig("dynawo");
        Path dynawoHomeDir = config.getPathProperty("dynawoHomeDir", null);
        Path workingDir = config.getPathProperty("workingDir", Paths.get("./tmp"));
        boolean debug = config.getBooleanProperty("debug", false);
        String dynawoCptCommandName = config.getStringProperty("dynawoCptCommandName", DEFAULT_DYNAWO_CMD_NAME);
        return new DynawoConfig(dynawoHomeDir, workingDir, debug, dynawoCptCommandName);
    }

    public DynawoConfig(Path dynawoHomeDir, Path workingDir, boolean debug, String dynawoCptCommandName) {
        this.dynawoHomeDir = dynawoHomeDir;
        this.workingDir = workingDir;
        this.debug = debug;
        this.dynawoCptCommandName = dynawoCptCommandName;
    }

    public Path getDynawoHomeDir() {
        return dynawoHomeDir;
    }

    public Path getWorkingDir() {
        return workingDir;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getDynawoCptCommandName() {
        return dynawoCptCommandName;
    }

    private final Path dynawoHomeDir;
    private final Path workingDir;
    private final boolean debug;
    private final String dynawoCptCommandName;
}
