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
public class NetworkModel {

    private final DefaultBusModel busModel = new DefaultBusModel();
    private final DefaultLineModel lineModel1 = new DefaultLineModel(Branch.Side.ONE);
    private final DefaultLineModel lineModel2 = new DefaultLineModel(Branch.Side.TWO);
    private final DefaultGeneratorModel generatorModel = new DefaultGeneratorModel();

    public BlackBoxModel getDefaultBusModel() {
        return busModel;
    }

    public BlackBoxModel getDefaultLineModel(Branch.Side side) {
        return side == Branch.Side.ONE ? lineModel1 : lineModel2;
    }

    public BlackBoxModel getDefaultGeneratorModel() {
        return generatorModel;
    }
}
