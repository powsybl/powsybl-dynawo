/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.defaultmodels;

import com.powsybl.dynawo.models.InjectionModel;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractInjectionDefaultModel extends AbstractDefaultModel implements InjectionModel {

    protected AbstractInjectionDefaultModel(String staticId) {
        super(staticId);
    }

    public String getStateValueVarName() {
        return "@NAME@_state_value";
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return getStateValueVarName();
    }
}
