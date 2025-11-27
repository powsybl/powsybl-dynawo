/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.computation.ExecutionEnvironment;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ExecutionEnvironmentUtils {

    private static final String VERSION_INFIX = "version_";

    private ExecutionEnvironmentUtils() {
    }

    public static ExecutionEnvironment createVersionEnv(AbstractDynawoConfig config, String workingDir) {
        return createSimulationEnv(config, workingDir + VERSION_INFIX, null);
    }

    public static ExecutionEnvironment createVersionEnv(AbstractDynawoConfig config, String workingDir, String dumpDir) {
        return createSimulationEnv(config, workingDir + VERSION_INFIX, dumpDir);
    }

    public static ExecutionEnvironment createSimulationEnv(AbstractDynawoConfig config, String workingDir) {
        return createSimulationEnv(config, workingDir, null);
    }

    public static ExecutionEnvironment createSimulationEnv(AbstractDynawoConfig config, String workingDir, String dumpDir) {
        return new ExecutionEnvironment(config.createEnv(), workingDir, config.isDebug(), dumpDir);
    }
}
