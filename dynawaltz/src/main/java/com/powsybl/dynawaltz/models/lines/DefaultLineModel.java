/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.lines;

import com.powsybl.dynawaltz.models.AbstractNetworkModel;
import com.powsybl.iidm.network.Branch;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class DefaultLineModel extends AbstractNetworkModel implements LineModel {
    private final String sideSuffix;

    public DefaultLineModel(String staticId, Branch.Side side) {
        super(staticId);
        this.sideSuffix = LineModel.getSuffix(side);
    }

    @Override
    public String getName() {
        return "NetworkLine" + sideSuffix;
    }

    @Override
    public String getIVarName() {
        return "@NAME@_i" + sideSuffix;
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
    public String getDesactivateCurrentLimitsVarName() {
        return "@NAME@_desactivate_currentLimits";
    }
}