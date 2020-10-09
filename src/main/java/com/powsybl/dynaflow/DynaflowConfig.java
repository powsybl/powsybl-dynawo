package com.powsybl.dynaflow;

import com.google.common.collect.ImmutableMap;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.computation.Command;
import com.powsybl.computation.SimpleCommandBuilder;

import java.nio.file.Path;
import java.util.*;

import static com.powsybl.dynaflow.DynaflowConstants.CONFIG_FILENAME;
import static com.powsybl.dynaflow.DynaflowConstants.IIDM_IN_FILE;

public class DynaflowConfig {
    private static final boolean DEFAULT_DEBUG = false;
    private final Path homeDir;
    private final boolean debug;

    public DynaflowConfig(Path homeDir, boolean debug) {
        this.homeDir = homeDir;
        this.debug = debug;
    }

    public static DynaflowConfig fromPropertyFile() {
        return fromPlatformConfig(PlatformConfig.defaultConfig());
    }

    public static DynaflowConfig fromPlatformConfig(PlatformConfig platformConfig) {
        Objects.requireNonNull(platformConfig);
        ModuleConfig config = platformConfig.getModuleConfig("dynaflow");
        Path homeDir = config.getPathProperty("homeDir");

        boolean debug = config.getBooleanProperty("debug", DEFAULT_DEBUG);
        return new DynaflowConfig(homeDir, debug);
    }

    public Map<String, String> createEnv() {
        return ImmutableMap.<String, String>builder()
                .build();
    }

    private String getProgram() {
        return getHomeDir().resolve("dynaflow-launcher.sh").toString();
    }

    public Command getCommand(Path workingDir) {
        String iidmPath = workingDir.resolve(IIDM_IN_FILE).toString();
        String configPath = workingDir.resolve(CONFIG_FILENAME).toString();
        List<String> args = Arrays.asList("--iidm", iidmPath, "--config", configPath);

        return new SimpleCommandBuilder()
                .id("dynaflow_lf")
                .program(getProgram())
                .args(args)
                .build();
    }

    public Command getVersionCommand() {
        List<String> args = Arrays.asList("--version");
        return new SimpleCommandBuilder()
                .id("dynaflow_version")
                .program(getProgram())
                .args(args)
                .build();
    }

    public boolean isDebug() {
        return debug;
    }

    public Path getHomeDir() {
        return homeDir;
    }

}
