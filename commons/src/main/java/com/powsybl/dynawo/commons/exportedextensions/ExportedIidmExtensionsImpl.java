/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.exportedextensions;

import com.google.auto.service.AutoService;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExportedIidmExtensions.class)
public class ExportedIidmExtensionsImpl implements ExportedIidmExtensions {

    private static final List<String> IIDM_EXTENSIONS = List.of(
            "activePowerControl",
            "slackTerminal",
            "coordinatedReactiveControl",
            "hvdcAngleDroopActivePowerControl",
            "hvdcOperatorActivePowerRange",
            "standbyAutomaton");

    @Override
    public List<String> getIidmExtensionNames() {
        return IIDM_EXTENSIONS;
    }
}
