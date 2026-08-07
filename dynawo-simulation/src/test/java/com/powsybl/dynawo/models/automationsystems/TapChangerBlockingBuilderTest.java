/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.automationsystems;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.PowsyblTestReportResourceBundle;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class TapChangerBlockingBuilderTest {

    private ReportNode reportNode;

    @BeforeEach
    void setUp() {
        reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testBuilder")
                .build();
    }

    @Test
    void testVoltageLevelsInput() throws IOException {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        TapChangerBlockingAutomationSystem tcb = TapChangerBlockingAutomationSystemBuilder.of(network, reportNode)
                .dynamicModelId("BBM_TCB")
                .parameterSetId("tcb_par")
                .transformersVoltageLevels("VLLOAD", "VLHV1")
                .uMeasurements("NHV1")
                .build();
        assertThat(tcb).hasFieldOrPropertyWithValue("tapChangerEquipments",
                List.of(network.getTwoWindingsTransformer("NHV2_NLOAD"),
                        network.getLoad("LOAD"),
                        network.getTwoWindingsTransformer("NGEN_NHV1")));
        assertReport("""
                + Builder tests
                   Model TapChangerBlockingAutomationSystem BBM_TCB instantiation OK
                """, reportNode);
    }

    @Test
    void testEmptyVoltageLevelsInput() throws IOException {
        Network network = EurostagTutorialExample1Factory.createWith3wTransformer();
        TapChangerBlockingAutomationSystem tcb = TapChangerBlockingAutomationSystemBuilder.of(network, reportNode)
                .dynamicModelId("BBM_TCB")
                .parameterSetId("tcb_par")
                .transformersVoltageLevels("V2")
                .uMeasurements("NHV1")
                .build();
        assertNull(tcb);
        assertReport("""
                + Builder tests
                   + Model TapChangerBlockingAutomationSystem BBM_TCB instantiation KO
                      'transformers' list is empty
                      'uMeasurements' field value 'NHV1' should be energized and in main connected component
                """, reportNode);
    }

    @Test
    void testWrongVoltageLevelsInput() throws IOException {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        TapChangerBlockingAutomationSystem tcb = TapChangerBlockingAutomationSystemBuilder.of(network, reportNode)
                .dynamicModelId("BBM_TCB")
                .parameterSetId("tcb_par")
                .transformersVoltageLevels(List.of("WRONG_ID", "WRONG_ID2"))
                .uMeasurements("NHV1")
                .build();
        assertNull(tcb);
        assertReport("""
                + Builder tests
                   + Model TapChangerBlockingAutomationSystem BBM_TCB instantiation KO
                      'transformersVoltageLevels' field value 'WRONG_ID' not found for equipment type(s) VOLTAGE_LEVEL
                      'transformersVoltageLevels' field value 'WRONG_ID2' not found for equipment type(s) VOLTAGE_LEVEL
                      'transformers' list is empty
                """, reportNode);
    }

    private void assertReport(String expectedReport, ReportNode reportNode) throws IOException {
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(expectedReport, TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
