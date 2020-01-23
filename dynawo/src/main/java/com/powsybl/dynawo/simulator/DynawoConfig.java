/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.util.Objects;

import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoConfig {

    public static DynawoConfig load() {
        return load(PlatformConfig.defaultConfig());
    }

    public static DynawoConfig load(PlatformConfig platformConfig) {
        DynawoConfig dynawoConfig = null;
        if (platformConfig.moduleExists("dynawo")) {
            ModuleConfig config = platformConfig.getModuleConfig("dynawo");
            String homeDir = config.getStringProperty("homeDir");
            boolean debug = config.getBooleanProperty("debug");
            dynawoConfig = new DynawoConfig(homeDir, debug);
        }
        return dynawoConfig;
    }

    public DynawoConfig(String homeDir, boolean debug) {
        this.homeDir = Objects.requireNonNull(homeDir);
        this.debug = debug;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public boolean isDebug() {
        return debug;
    }

    private final String homeDir;
    private final boolean debug;
}
