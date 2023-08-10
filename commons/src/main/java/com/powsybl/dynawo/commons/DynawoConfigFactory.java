/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.PlatformConfig;

import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class DynawoConfigFactory {

    private static final boolean DEBUG_DEFAULT = false;

    private DynawoConfigFactory() {
    }

    public static SimpleDynawoConfig load(String moduleName) {
        return load(PlatformConfig.defaultConfig(), moduleName);
    }

    public static SimpleDynawoConfig load(PlatformConfig platformConfig, String moduleName) {
        Objects.requireNonNull(platformConfig);
        return platformConfig.getOptionalModuleConfig(moduleName)
                .map(moduleConfig -> new SimpleDynawoConfig(
                        moduleConfig.getPathProperty("homeDir"),
                        moduleConfig.getBooleanProperty("debug", DEBUG_DEFAULT)))
                .orElseThrow(() -> new PowsyblException("PlatformConfig incomplete: Module " + moduleName + " not found"));
    }
}
