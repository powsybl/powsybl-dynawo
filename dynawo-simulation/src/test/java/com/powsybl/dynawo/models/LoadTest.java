/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.NoEquipmentNetworkFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class LoadTest {

    private static final String FICTITIOUS_REPORT =
        """
        + Load tests
           'staticId' field value 'LOAD' should not be fictitious
           Model load cannot be instantiated
        """;

    @Test
    void loadFictitious() throws IOException {
        ReportNode reportNode = ReportNode.newRootReportNode().withMessageTemplate("loadTest", "Load tests").build();
        Network network = NoEquipmentNetworkFactory.create();
        network.getVoltageLevel("vl1").newLoad()
                .setId("LOAD")
                .setBus("busA")
                .setConnectableBus("busA")
                .setFictitious(true)
                .setP0(600.0)
                .setQ0(200.0)
                .add();
        assertNull(BaseLoadBuilder.of(network, reportNode)
                .dynamicModelId("load")
                .staticId("LOAD")
                .parameterSetId("LAB")
                .build());
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(FICTITIOUS_REPORT, TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
