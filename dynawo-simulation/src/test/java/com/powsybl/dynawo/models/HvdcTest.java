/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcVscBuilder;
import com.powsybl.dynawo.models.hvdc.BaseHvdc;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class HvdcTest {

    @Test
    void testConnectedStation() {
        Network network = HvdcTestNetwork.createVsc();
        BaseHvdc hvdc = HvdcPBuilder.of(network, "HvdcPV")
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
        BaseHvdc hvdcVscDangling = HvdcVscBuilder.of(network, "HvdcVSCDanglingP")
                .dynamicModelId("hvdc")
                .staticId("L")
                .parameterSetId("HVDC")
                .build();
        assertEquals(1, hvdcVscDangling.getConnectedStations().size());
        assertEquals(line.getConverterStation1(), hvdcVscDangling.getConnectedStations().get(0));
    }

    @Test
    void vscDynamicModelOnLCC() {
        Network network = HvdcTestNetwork.createLcc();
        assertNull(HvdcVscBuilder.of(network)
                .dynamicModelId("hvdc")
                .staticId("L")
                .parameterSetId("HVDC")
                .build());
    }

    @Test
    void testDefaultDanglingSide() {
        ReportNode reportNode = ReportNode.newRootReportNode()
                .withAllResourceBundlesFromClasspath()
                .withMessageTemplate("hvdcBuilder", "HVDC builder")
                .build();
        Network network = HvdcTestNetwork.createVsc();
        HvdcLine line = network.getHvdcLine("L");

        // dangling side ONE replaced by side TWO
        BaseHvdc hvdcPDangling = Objects.requireNonNull(
                HvdcPBuilder.of(network, "HvdcPVDangling", reportNode))
                    .dynamicModelId("hvdc")
                    .staticId("L")
                    .parameterSetId("HVDC")
                    .dangling(TwoSides.ONE)
                    .build();
        assertEquals(1, hvdcPDangling.getConnectedStations().size());
        assertEquals(line.getConverterStation1(), hvdcPDangling.getConnectedStations().get(0));
        assertThat(reportNode.getChildren().stream()
                .filter(r -> r.getMessageKey().equalsIgnoreCase("fieldOptionNotImplemented"))
                .findFirst())
                .isNotEmpty()
                .get()
                .hasFieldOrPropertyWithValue("message",
                        "'dangling' field is set but this option is not implemented yet, default value TWO will be used");
    }
}
