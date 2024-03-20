/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynawaltz.models.lines.LineBuilder;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.PhaseShifterTestCaseFactory;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class BuilderEquipmentSetterTest {

    @Test
    void addEquipmentFromAnotherNetwork() throws IOException {

        Network network = SvcTestCaseFactory.create();
        Line ln1 = network.getLine("L1");
        Network network2 = PhaseShifterTestCaseFactory.create();
        Line ln2 = network2.getLine("L1");
        ReportNode reportNode = ReportNode.newRootReportNode().withMessageTemplate("builderTests", "Builder tests").build();

        BlackBoxModel bbm1 = LineBuilder.of(network)
                .dynamicModelId("BBM_LINE_NETWORK_1")
                .equipment(ln1)
                .parameterSetId("sl")
                .build();
        BlackBoxModel bbm2 = LineBuilder.of(network, reportNode)
                .dynamicModelId("BBM_LINE_NETWORK_2")
                .equipment(ln2)
                .parameterSetId("sl")
                .build();

        assertNotNull(bbm1);
        assertNull(bbm2);
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals("""
                        + Builder tests
                           'equipment' field value LINE L1 does not belong to the builder network
                           Model BBM_LINE_NETWORK_2 cannot be instantiated
                        """,
                TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
