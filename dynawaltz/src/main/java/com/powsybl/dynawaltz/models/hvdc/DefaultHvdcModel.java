/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.dynawaltz.models.AbstractNetworkModel;
import com.powsybl.dynawaltz.models.Side;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class DefaultHvdcModel extends AbstractNetworkModel implements HvdcModel {

    public DefaultHvdcModel(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "NetworkHvdc";
    }

    @Override
    public String getSwitchOffSignalEventVarName(Side side) {
        return "@NAME@_state" + side.getSideNumber() + "_value";
    }
}