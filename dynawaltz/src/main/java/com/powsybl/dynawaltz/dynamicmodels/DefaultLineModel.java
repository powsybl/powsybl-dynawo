/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.iidm.network.Branch;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class DefaultLineModel extends AbstractNetworkBlackBoxModel implements LineModel {
    private final String sidePostfix;

    public DefaultLineModel(Branch.Side side) {
        this.sidePostfix = LineModel.getPostfix(side);
    }

    @Override
    public String getLib() {
        return "NetworkLine" + sidePostfix;
    }

    @Override
    public String getIVarName() {
        return "@NAME@_i" + sidePostfix;
    }

    @Override
    public String getStateVarName() {
        return "@NAME@_state";
    }

    @Override
    public String getDesactivateCurrentLimitsVarName() {
        return "@NAME@_desactivate_currentLimits";
    }
}
