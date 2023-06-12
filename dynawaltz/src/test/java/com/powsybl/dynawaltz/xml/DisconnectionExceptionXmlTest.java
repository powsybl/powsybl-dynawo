/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.events.EventHvdcDisconnection;
import com.powsybl.dynawaltz.models.hvdc.HvdcPvDangling;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class DisconnectionExceptionXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    @BeforeEach
    void setup() {
        setupNetwork();
        addDynamicModels();
    }

    @Override
    protected void setupNetwork() {
        network = HvdcTestNetwork.createVsc();
    }

    @Override
    protected void addDynamicModels() {
        HvdcLine hvdc = network.getHvdcLine("L");
        dynamicModels.add(new HvdcPvDangling("BBM_L", hvdc, "hvdc", "HvdcPVDangling", Side.ONE));
        eventModels.add(new EventHvdcDisconnection(hvdc, 1, true, false));
    }

    @Test
    void disconnectionOnDanglingSide() {
        Exception e = assertThrows(PowsyblException.class, this::setupDynawaltzContext);
        assertEquals("Equipment HvdcPVDangling side 1 is dangling and can't be disconnected with an event", e.getMessage());
    }
}
