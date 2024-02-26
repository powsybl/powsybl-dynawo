/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public abstract class AbstractDynawoConfig {

    private static final boolean DEBUG_DEFAULT = false;

    protected static <T extends AbstractDynawoConfig> T load(Function<ModuleConfig, T> configFactory, String moduleName) {
        return load(configFactory, moduleName, PlatformConfig.defaultConfig());
    }

    protected static <T extends AbstractDynawoConfig> T load(Function<ModuleConfig, T> configFactory, String moduleName, PlatformConfig platformConfig) {
        return platformConfig.getOptionalModuleConfig(moduleName)
                .map(configFactory)
                .orElseThrow(() -> new PowsyblException("PlatformConfig incomplete: Module " + moduleName + " not found"));
    }

    private final Path homeDir;
    private final boolean debug;

    protected AbstractDynawoConfig(Path homeDir, boolean debug) {
        this.homeDir = Objects.requireNonNull(homeDir);
        this.debug = debug;
    }

    protected AbstractDynawoConfig(ModuleConfig config) {
        this(config.getPathProperty("homeDir"), config.getBooleanProperty("debug", DEBUG_DEFAULT));
    }

    public Path getHomeDir() {
        return homeDir;
    }

    public boolean isDebug() {
        return debug;
    }

    public abstract String getProgram();

    public String getProgram(String programName) {
        String extension = SystemUtils.IS_OS_WINDOWS ? ".cmd" : ".sh";
        return homeDir.resolve(programName + extension).toString();
    }
}
