/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.PowsyblTestReportResourceBundle;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ModelVersionTest {

    private final Network network = EurostagTutorialExample1Factory.create();
    private ReportNode reportNode;

    @BeforeEach
    void setup() {
        reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testBuilder")
                .build();
    }

    @Test
    void testCurrentVersionTooHigh() throws IOException {
        ModelConfigsHandler.getInstance().setDynawoVersion(new DynawoVersion(1, 7, 0));
        SynchronousGeneratorBuilder builder = SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindingsGovSteam1ExcIEEEST4B", reportNode);
        assertNull(builder);
        checkReport("""
                + Builder tests
                   Model GeneratorSynchronousFourWindingsGovSteam1ExcIEEEST4B version 1.6.0 is too low for the current Dynawo version 1.7.0 (Renamed GeneratorSynchronousFourWindingsGovSteam1St4b)
                """);
    }

    @Test
    void testCurrentVersionTooLow() throws IOException {
        ModelConfigsHandler.getInstance().setDynawoVersion(new DynawoVersion(1, 5, 0));
        SynchronousGeneratorBuilder builder = SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousThreeWindingsIeeeG1IeeeT1Tfo", reportNode);
        assertNull(builder);
        checkReport("""
                + Builder tests
                   Model GeneratorSynchronousThreeWindingsIeeeG1IeeeT1Tfo version 1.6.0 is too high for the current Dynawo version 1.5.0
                """);
    }

    private void checkReport(String report) throws IOException {
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(report, TestUtil.normalizeLineSeparator(sw.toString()));
    }

    @AfterEach
    void tearDown() {
        ModelConfigsHandler.getInstance().setDynawoVersion(DynawoConstants.CURRENT_VERSION);
    }
}
