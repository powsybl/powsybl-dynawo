/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.switches;

import com.powsybl.dynawo.models.defaultmodels.AbstractDefaultModel;
import com.powsybl.dynawo.models.versionablevariable.VersionableVariables;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DefaultSwitch extends AbstractDefaultModel implements SwitchModel {

    public DefaultSwitch(String staticId) {
        super(staticId);
    }

    @Override
    public String getStateValueVarName() {
        return String.format(VersionableVariables.getCurrentValue("STATE"), "@NAME@");
    }

    @Override
    public String getName() {
        return "DefaultSwitch";
    }
}
