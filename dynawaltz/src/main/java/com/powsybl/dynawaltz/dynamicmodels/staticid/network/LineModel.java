/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels.staticid.network;

import com.powsybl.iidm.network.Branch;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public interface LineModel {
    String getIVarName();

    String getStateVarName();

    String getDesactivateCurrentLimitsVarName();

    String getStateValueVarName();

    static String getPostfix(Branch.Side side) {
        switch (side) {
            case ONE:
                return "Side1";
            case TWO:
                return "Side2";
            default:
                throw new AssertionError("Unexpected Side value: " + side);
        }
    }
}
