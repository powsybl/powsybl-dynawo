/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.dynawaltz.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawaltz.models.hvdc.HvdcVscBuilder;
import com.powsybl.dynawaltz.models.hvdc.HvdcP;
import com.powsybl.dynawaltz.models.hvdc.HvdcVsc;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;
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
        HvdcP hvdc = HvdcPBuilder.of(network, "HvdcPV")
                .dynamicModelId("hvdc")
                .staticId("L")
                .parameterSetId("HVDC")
                .build();
        assertEquals(2, hvdc.getConnectedStations().size());
    }

    @Test
    void testDanglingConnectedStation() {
        Network network = HvdcTestNetwork.createVsc();
        HvdcLine line = network.getHvdcLine("L");

        HvdcP hvdcPDangling = HvdcPBuilder.of(network, "HvdcPVDangling")
                .dynamicModelId("hvdc")
                .staticId("L")
                .parameterSetId("HVDC")
                .dangling(TwoSides.ONE)
                .build();
        assertEquals(1, hvdcPDangling.getConnectedStations().size());
        assertEquals(line.getConverterStation2(), hvdcPDangling.getConnectedStations().get(0));

        HvdcVsc hvdcVscDangling = HvdcVscBuilder.of(network, "HvdcVSCDanglingP")
                .dynamicModelId("hvdc")
                .staticId("L")
                .parameterSetId("HVDC")
                .dangling(TwoSides.TWO)
                .build();
        assertEquals(1, hvdcVscDangling.getConnectedStations().size());
        assertEquals(line.getConverterStation1(), hvdcVscDangling.getConnectedStations().get(0));
    }
}
