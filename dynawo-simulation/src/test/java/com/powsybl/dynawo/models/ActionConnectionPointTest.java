/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models;

import com.powsybl.dynawo.builders.BuildersUtil;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ActionConnectionPointTest {

    @Test
    void voltageOffBus() {
        Network network = EurostagTutorialExample1Factory.create();
        assertNull(BuildersUtil.getActionConnectionPoint(network, "NGEN"));
    }

    @Test
    void voltageOffBusBarSection() {
        Network network = HvdcTestNetwork.createBase();
        assertNull(BuildersUtil.getActionConnectionPoint(network, "BBS1"));
    }
}
