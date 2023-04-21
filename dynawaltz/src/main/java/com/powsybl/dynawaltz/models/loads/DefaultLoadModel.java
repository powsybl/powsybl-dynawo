/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.loads;

import com.powsybl.dynawaltz.models.AbstractNetworkModel;
import com.powsybl.dynawaltz.models.events.DisconnectableEquipment;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class DefaultLoadModel extends AbstractNetworkModel implements LoadModel, DisconnectableEquipment {

    public DefaultLoadModel(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "NetworkLoad";
    }

    public String getStateValueVarName() {
        return "@NAME@_state_value";
    }

    @Override
    public String getDisconnectableVarName() {
        return getStateValueVarName();
    }
}
