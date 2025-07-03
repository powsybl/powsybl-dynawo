/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.commons.AbstractDynawoConfig;

import java.nio.file.Path;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public class DynawoSimulationConfig extends AbstractDynawoConfig {

    public static final String DYNAWO_LAUNCHER_PROGRAM_NAME = "dynawo";
    private static final String DYNAWO_MODULE_NAME = "dynawo";

    public static DynawoSimulationConfig load() {
        return load(DynawoSimulationConfig::new, DYNAWO_MODULE_NAME);
    }

    public static DynawoSimulationConfig load(PlatformConfig platformConfig) {
        return load(DynawoSimulationConfig::new, DYNAWO_MODULE_NAME, platformConfig);
    }

    public DynawoSimulationConfig(Path homeDir, boolean debug) {
        super(homeDir, debug);
    }

    protected DynawoSimulationConfig(ModuleConfig config) {
        super(config);
    }

    @Override
    public String getProgram() {
        return getProgram(DYNAWO_LAUNCHER_PROGRAM_NAME);
    }
}
