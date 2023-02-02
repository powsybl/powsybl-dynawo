/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.PlatformConfig;

import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzConfig {

    public static DynaWaltzConfig load() {
        return load(PlatformConfig.defaultConfig());
    }

    public static DynaWaltzConfig load(PlatformConfig platformConfig) {
        return platformConfig.getOptionalModuleConfig("dynawaltz")
                .map(moduleConfig -> new DynaWaltzConfig(
                        moduleConfig.getStringProperty("homeDir"),
                        moduleConfig.getBooleanProperty("debug", DEBUG_DEFAULT)))
                .orElseThrow(() -> new PowsyblException("PlatformConfig incomplete: Module dynawaltz not found"));
    }

    public DynaWaltzConfig(String homeDir, boolean debug) {
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

    private static final boolean DEBUG_DEFAULT = false;
}
