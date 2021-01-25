/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.common.collect.ImmutableMap;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaflowConfig {

    private static final boolean DEFAULT_DEBUG = false;

    private final Path homeDir;
    private final boolean debug;

    public DynaflowConfig(Path homeDir, boolean debug) {
        this.homeDir = Objects.requireNonNull(homeDir);
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

    public boolean isDebug() {
        return debug;
    }

    public Path getHomeDir() {
        return homeDir;
    }
}
