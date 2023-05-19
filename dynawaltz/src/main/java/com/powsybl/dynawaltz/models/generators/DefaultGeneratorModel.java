/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.dynawaltz.models.AbstractNetworkModel;
import com.powsybl.dynawaltz.models.events.ControllableEquipment;
import com.powsybl.dynawaltz.models.events.DisconnectableEquipment;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class DefaultGeneratorModel extends AbstractNetworkModel implements GeneratorModel, DisconnectableEquipment, ControllableEquipment {

    public DefaultGeneratorModel(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "NetworkGenerator";
    }

    public String getStateValueVarName() {
        return "@NAME@_state_value";
    }

    @Override
    public String getDisconnectableVarName() {
        return getStateValueVarName();
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

    @Override
    public String getUPuVarName() {
        return "@NAME@_UPu";
    }

    @Override
    public String getDeltaPVarName() {
        return "@NAME@_Pc";
    }
}
