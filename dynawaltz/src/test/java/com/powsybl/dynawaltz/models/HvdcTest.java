/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.dynawaltz.models.hvdc.HvdcP;
import com.powsybl.dynawaltz.models.hvdc.HvdcPDangling;
import com.powsybl.dynawaltz.models.hvdc.HvdcVscDangling;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class HvdcTest {

    @Test
    void testConnectedStation() {
        Network network = HvdcTestNetwork.createVsc();
        HvdcP hvdc = new HvdcP("hvdc", network.getHvdcLine("L"), "HVDC", "HvdcPV");
        assertEquals(2, hvdc.getConnectedStations().size());
    }

    @Test
    void testDanglingConnectedStation() {
        Network network = HvdcTestNetwork.createVsc();
        HvdcLine line = network.getHvdcLine("L");

        HvdcPDangling hvdc = new HvdcPDangling("hvdc", line, "HVDC", "HvdcPVDangling", Side.ONE);
        assertEquals(1, hvdc.getConnectedStations().size());
        assertEquals(line.getConverterStation2(), hvdc.getConnectedStations().get(0));

        HvdcVscDangling hvdc2 = new HvdcVscDangling("hvdc", line, "HVDC", "HvdcVSCDanglingP", Side.TWO);
        assertEquals(1, hvdc.getConnectedStations().size());
        assertEquals(line.getConverterStation1(), hvdc2.getConnectedStations().get(0));
    }
}
