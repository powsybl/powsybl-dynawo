/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.lines;

import com.powsybl.dynawo.models.defaultmodels.AbstractDefaultModel;
import com.powsybl.dynawo.models.utils.SideUtils;
import com.powsybl.iidm.network.TwoSides;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public class DefaultLine extends AbstractDefaultModel implements LineModel {

    public DefaultLine(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "DefaultLine";
    }

    @Override
    public String getIVarName(TwoSides side) {
        return "@NAME@_i" + SideUtils.getSideSuffix(side);
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
