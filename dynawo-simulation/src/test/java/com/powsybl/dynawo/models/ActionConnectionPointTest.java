/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynawo.models.automationsystems.TapChangerBlockingAutomationSystemBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ActionConnectionPointTest {

    private ReportNode reportNode;

    @BeforeEach
    void setUp() {
        reportNode = ReportNode.newRootReportNode().withMessageTemplate("pointTest", "Action connection point tests").build();
    }

    @Test
    void voltageOffBus() throws IOException {
        Network network = EurostagTutorialExample1Factory.create();
        assertNull(TapChangerBlockingAutomationSystemBuilder.of(network, reportNode)
                .dynamicModelId("TC")
                .parameterSetId("tc")
                .transformers("NGEN_NHV1")
                .uMeasurements("NGEN")
                .build());
        testReport("""
                + Action connection point tests
                   'uMeasurements' field value 'NGEN' should be energized
                   Model TC cannot be instantiated
                """);
    }

    @Test
    void voltageOffBusBarSection() throws IOException {
        Network network = HvdcTestNetwork.createBase();
        assertNull(TapChangerBlockingAutomationSystemBuilder.of(network, reportNode)
                .dynamicModelId("TC")
                .parameterSetId("tc")
                .uMeasurements("BBS1")
                .build());
        testReport("""
                + Action connection point tests
                   'transformers' field is not set
                   'uMeasurements' field value 'BBS1' should be energized
                   Model TC cannot be instantiated
                """);
    }

    private void testReport(String report) throws IOException {
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(report, TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
