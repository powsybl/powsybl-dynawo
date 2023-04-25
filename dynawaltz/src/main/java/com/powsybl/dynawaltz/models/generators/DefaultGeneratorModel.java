/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.dynawaltz.models.AbstractInjectionNetworkModel;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class DefaultGeneratorModel extends AbstractInjectionNetworkModel implements GeneratorModel {

    public DefaultGeneratorModel(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "NetworkGenerator";
    }

    @Override
    public String getTerminalVarName() {
        return "@NAME@_terminal";
    }

    @Override
    public String getSwitchOffSignalNodeVarName() {
        return "@NAME@_switchOffSignal1";
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "@NAME@_switchOffSignal2";
    }

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return "@NAME@_switchOffSignal3";
    }

    @Override
    public String getRunningVarName() {
        return "@NAME@_running";
    }

    @Override
    public String getQStatorPuVarName() {
        return "@NAME@_QStatorPu";
    }

    public String getUPuVarName() {
        return "@NAME@_UPu";
    }
}
