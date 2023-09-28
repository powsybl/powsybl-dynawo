/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.shunts;

import com.powsybl.dynawaltz.models.defaultmodels.AbstractInjectionDefaultModel;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class DefaultShunt extends AbstractInjectionDefaultModel implements ShuntModel {

    public DefaultShunt(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "DefaultShunt";
    }

    @Override
    public String getStateVarName() {
        return "@NAME@_state";
    }

    @Override
    public String getIsCapacitorVarName() {
        return "@NAME@_isCapacitor";
    }

    @Override
    public String getIsAvailableVarName() {
        return "@NAME@_isAvailable";
    }
}
