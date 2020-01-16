package com.powsybl.dynawo.simulator;

import java.util.Objects;

import com.powsybl.commons.config.PlatformConfig;

public class DynawoConfig {

    public static final String HOME_DIR = "./dynawo";
    public static final boolean DEBUG_MODE = false;

    public static DynawoConfig load() {
        return load(PlatformConfig.defaultConfig());
    }

    public static DynawoConfig load(PlatformConfig platformConfig) {
        DynawoConfig dynawoConfig = new DynawoConfig();
        load(dynawoConfig, platformConfig);

        return dynawoConfig;
    }

    protected static void load(DynawoConfig dynawoConfig) {
        load(dynawoConfig, PlatformConfig.defaultConfig());
    }

    protected static void load(DynawoConfig dynawoConfig, PlatformConfig platformConfig) {
        Objects.requireNonNull(dynawoConfig);
        Objects.requireNonNull(platformConfig);

        platformConfig.getOptionalModuleConfig("dynawo")
            .ifPresent(config -> {
                dynawoConfig.setHomeDir(config.getStringProperty("homeDir", HOME_DIR));
                dynawoConfig.setDebug(config.getBooleanProperty("debug", DEBUG_MODE));
            });
    }

    public DynawoConfig() {
        this(HOME_DIR, DEBUG_MODE);
    }

    public DynawoConfig(String homeDir, boolean debug) {
        this.homeDir = homeDir;
        this.debug = debug;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private String homeDir;
    private boolean debug;
}
