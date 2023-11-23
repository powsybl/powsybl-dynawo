/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.TwoSides;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class HvdcVsc extends AbstractHvdc {

    public HvdcVsc(String dynamicModelId, HvdcLine hvdc, String parameterSetId, String hvdcLib) {
        super(dynamicModelId, hvdc, parameterSetId, hvdcLib);
    }

    @Override
    public String getSwitchOffSignalEventVarName(TwoSides side) {
        return "hvdc_Conv" + side.getNum() + "_switchOffSignal2";
    }
}
