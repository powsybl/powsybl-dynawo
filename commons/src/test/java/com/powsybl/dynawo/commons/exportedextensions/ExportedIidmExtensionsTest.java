/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.exportedextensions;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ExportedIidmExtensionsTest {

    @Test
    void loadExtensionNames() {
        List<String> extensionNames = new ExportedIidmExtensionsHandler().getExtensionNames();
        assertThat(extensionNames).containsExactly(
                "activePowerControl",
                "slackTerminal",
                "coordinatedReactiveControl",
                "hvdcAngleDroopActivePowerControl",
                "hvdcOperatorActivePowerRange",
                "standbyAutomaton");
    }
}
