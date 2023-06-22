/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.lines;

import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.defaultmodels.AbstractDefaultModel;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class DefaultLineModel extends AbstractDefaultModel implements LineModel {

    public DefaultLineModel(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "DefaultLine";
    }

    @Override
    public String getIVarName(Side side) {
        return "@NAME@_i" + side.getSideSuffix();
    }

    @Override
    public String getStateVarName() {
        return "@NAME@_state";
    }

    @Override
    public String getStateValueVarName() {
        return "@NAME@_state_value";
    }

    @Override
    public String getDeactivateCurrentLimitsVarName() {
        return "@NAME@_desactivate_currentLimits";
    }
}
