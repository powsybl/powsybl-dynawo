/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.buses;

import com.powsybl.dynawaltz.models.AbstractNetworkModel;

import java.util.Optional;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class DefaultBusModel extends AbstractNetworkModel implements BusModel {
    public DefaultBusModel(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "NetworkBus";
    }

    @Override
    public String getTerminalVarName() {
        return "@NAME@_ACPIN";
    }

    @Override
    public String getSwitchOffSignalVarName() {
        return "@NAME@_switchOff";
    }

    @Override
    public String getNumCCVarName() {
        return "@NAME@_numcc";
    }

    @Override
    public Optional<String> getUImpinVarName() {
        return Optional.of("@NAME@_U");
    }

    @Override
    public Optional<String> getUpuImpinVarName() {
        return Optional.of("@NAME@_Upu");
    }
}
