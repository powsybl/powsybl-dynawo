/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.iidm.network.Branch;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class NetworkModel {

    private final Function<String, DefaultBusModel> busModelFactory = DefaultBusModel::new;
    private final BiFunction<String, Branch.Side, DefaultLineModel> lineModelFactory = DefaultLineModel::new;

    public BlackBoxModel getDefaultBusModel(String staticId) {
        return busModelFactory.apply(staticId);
    }

    public BlackBoxModel getDefaultLineModel(String staticId, Branch.Side side) {
        return lineModelFactory.apply(staticId, side);
    }
}
