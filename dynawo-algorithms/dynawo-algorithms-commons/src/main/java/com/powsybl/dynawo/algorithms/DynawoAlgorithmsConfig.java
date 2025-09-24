/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms;

import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.commons.AbstractDynawoConfig;

import java.nio.file.Path;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoAlgorithmsConfig extends AbstractDynawoConfig {

    public static final String DYNAWO_ALGORITHMS_LAUNCHER_PROGRAM_NAME = "dynawo-algorithms";
    protected static final String MODULE_NAME = "dynawo-algorithms";

    public static DynawoAlgorithmsConfig load() {
        return load(DynawoAlgorithmsConfig::new, MODULE_NAME);
    }

    public static DynawoAlgorithmsConfig load(PlatformConfig platformConfig) {
        return load(DynawoAlgorithmsConfig::new, MODULE_NAME, platformConfig);
    }

    public DynawoAlgorithmsConfig(Path homeDir, boolean debug) {
        super(homeDir, debug);
    }

    private DynawoAlgorithmsConfig(ModuleConfig config) {
        super(config);
    }

    @Override
    public String getProgram() {
        return getProgram(DYNAWO_ALGORITHMS_LAUNCHER_PROGRAM_NAME);
    }
}
