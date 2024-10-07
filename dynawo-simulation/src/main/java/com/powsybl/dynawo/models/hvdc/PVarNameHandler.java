/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.hvdc;

import com.powsybl.dynawo.models.utils.SideUtils;
import com.powsybl.iidm.network.TwoSides;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class PVarNameHandler implements HvdcVarNameHandler {

    @Override
    public String getConnectionPointVarName(TwoSides side) {
        return String.format("hvdc_switchOffSignal1%s", SideUtils.getSideSuffix(side));
    }

    @Override
    public String getEventVarName(TwoSides side) {
        return String.format("hvdc_switchOffSignal2%s", SideUtils.getSideSuffix(side));
    }
}
