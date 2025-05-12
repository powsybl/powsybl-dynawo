/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.PowsyblCoreTestReportResourceBundle;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
import com.powsybl.dynawo.models.lines.LineBuilder;
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
        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblCoreTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testBuilder")
                .build();

        BlackBoxModel bbmNetwork1 = LineBuilder.of(network)
                .equipment(ln1)
                .parameterSetId("sl")
                .build();
        BlackBoxModel bbmNetwork2 = LineBuilder.of(network, reportNode)
                .equipment(ln2)
                .parameterSetId("sl")
                .build();

        assertNotNull(bbmNetwork1);
        assertNull(bbmNetwork2);
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals("""
                        + Builder tests
                           + Model Line L1 instantiation KO
                              'equipment' field value LINE L1 does not belong to the builder network
                        """,
                TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
