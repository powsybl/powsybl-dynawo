/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.loads;

import com.powsybl.dynawo.models.events.ControllableEquipment;
import com.powsybl.iidm.network.Load;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseLoadControllable extends BaseLoad implements ControllableEquipment {

    protected BaseLoadControllable(String dynamicModelId, Load load, String parameterSetId, String lib) {
        super(dynamicModelId, load, parameterSetId, lib);
    }

    @Override
    public String getDeltaPVarName() {
        return "load_deltaP";
    }
}
