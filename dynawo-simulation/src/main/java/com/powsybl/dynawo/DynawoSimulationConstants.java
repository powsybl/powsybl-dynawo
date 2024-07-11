/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class DynawoSimulationConstants {

    private DynawoSimulationConstants() {
    }

    public static final String OUTPUTS_FOLDER = "outputs";
    public static final String FINAL_STATE_FOLDER = "finalState";

    public static String getSimulationParFile(Network network) {
        return network.getId() + ".par";
    }
}
