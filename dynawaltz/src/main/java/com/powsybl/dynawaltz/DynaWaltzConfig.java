/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.commons.DynawoConfig;

import java.nio.file.Path;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public class DynaWaltzConfig extends DynawoConfig {

    public static final String DYNAWALTZ_LAUNCHER_PROGRAM_NAME = "dynawo";
    private static final String DYNAWALTZ_MODULE_NAME = "dynawaltz";

    public static DynaWaltzConfig load() {
        return load(DynaWaltzConfig::new, DYNAWALTZ_MODULE_NAME);
    }

    public static DynaWaltzConfig load(PlatformConfig platformConfig) {
        return load(DynaWaltzConfig::new, DYNAWALTZ_MODULE_NAME, platformConfig);
    }

    public DynaWaltzConfig(Path homeDir, boolean debug) {
        super(homeDir, debug);
    }

    private DynaWaltzConfig(ModuleConfig config) {
        super(config);
    }

    public String getProgram() {
        return getProgram(DYNAWALTZ_LAUNCHER_PROGRAM_NAME);
    }
}
