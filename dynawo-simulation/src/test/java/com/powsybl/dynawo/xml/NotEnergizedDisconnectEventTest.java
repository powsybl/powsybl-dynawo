/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.Test;

import static com.powsybl.iidm.network.test.EurostagTutorialExample1Factory.NGEN;
import static com.powsybl.iidm.network.test.EurostagTutorialExample1Factory.NHV1_NHV2_1;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class NotEnergizedDisconnectEventTest {

    @Test
    void testBus() {
        Network network = EurostagTutorialExample1Factory.create();
        BlackBoxModel nullEvent = EventDisconnectionBuilder.of(network)
                .staticId(NGEN)
                .startTime(5)
                .build();
        assertNull(nullEvent);
    }

    @Test
    void testInjection() {
        Network network = EurostagTutorialExample1Factory.create();
        network.getGenerator("GEN").disconnect();
        BlackBoxModel nullEvent = EventDisconnectionBuilder.of(network)
                .staticId("GEN")
                .startTime(5)
                .build();
        assertNull(nullEvent);
    }

    @Test
    void testBranch() {
        Network network = EurostagTutorialExample1Factory.create();
        BlackBoxModel nullEvent = EventDisconnectionBuilder.of(network)
                .staticId(NHV1_NHV2_1)
                .startTime(5)
                .build();
        assertNull(nullEvent);
        nullEvent = EventDisconnectionBuilder.of(network)
                .staticId(NHV1_NHV2_1)
                .disconnectOnly(TwoSides.TWO)
                .startTime(5)
                .build();
        assertNull(nullEvent);
    }

    @Test
    void testHvdc() {
        Network network = HvdcTestNetwork.createVsc();
        BlackBoxModel nullEvent = EventDisconnectionBuilder.of(network)
                .staticId("L")
                .startTime(5)
                .build();
        assertNull(nullEvent);
        nullEvent = EventDisconnectionBuilder.of(network)
                .staticId("L")
                .disconnectOnly(TwoSides.TWO)
                .startTime(5)
                .build();
        assertNull(nullEvent);
    }
}
