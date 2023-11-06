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
import com.powsybl.dynawo.commons.DynawoConfig;

import java.nio.file.Path;
import java.util.Map;

/**
 *
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 */
public class DynaFlowConfig extends DynawoConfig {

    private static final String DYNAFLOW_MODULE_NAME = "dynaflow";

    public static DynaFlowConfig load() {
        return load(DynaFlowConfig::new, DYNAFLOW_MODULE_NAME);
    }

    public static DynaFlowConfig load(PlatformConfig platformConfig) {
        return load(DynaFlowConfig::new, DYNAFLOW_MODULE_NAME, platformConfig);
    }

    public DynaFlowConfig(Path homeDir, boolean debug) {
        super(homeDir, debug);
    }

    private DynaFlowConfig(ModuleConfig config) {
        super(config);
    }

    public Map<String, String> createEnv() {
        return ImmutableMap.<String, String>builder()
                .build();
    }
}
