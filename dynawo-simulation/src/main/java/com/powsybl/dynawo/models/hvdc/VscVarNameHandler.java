/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.hvdc;

import com.powsybl.iidm.network.TwoSides;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class VscVarNameHandler implements HvdcVarNameHandler {

    @Override
    public String getConnectionPointVarName(TwoSides side) {
        return String.format("hvdc_Conv%s_switchOffSignal1", side.getNum());
    }

    @Override
    public String getEventVarName(TwoSides side) {
        return String.format("hvdc_Conv%s_switchOffSignal2", side.getNum());
    }
}