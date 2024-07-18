/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models;

import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.NoEquipmentNetworkFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class LoadTest {

    @Test
    void loadFictitious() {
        Network network = NoEquipmentNetworkFactory.create();
        network.getVoltageLevel("vl1").newLoad()
                .setId("LOAD")
                .setBus("busA")
                .setConnectableBus("busA")
                .setFictitious(true)
                .setP0(600.0)
                .setQ0(200.0)
                .add();
        assertNull(BaseLoadBuilder.of(network)
                .dynamicModelId("load")
                .staticId("L")
                .parameterSetId("LAB")
                .build());
    }
}
