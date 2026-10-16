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
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcVscBuilder;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class AlternativeIdsTest {

    @Test
    void testAlternativeIds() throws IOException {
        Network network = EurostagTutorialExample1Factory.create();
        ReportNode reportNode = createReportNode();
        BlackBoxModel bbm = Objects.requireNonNull(BaseGeneratorBuilder.of(network, "GeneratorPVFixed", reportNode))
                .staticId("ALT_ID", "ALT_ID2", "GEN")
                .build();
        assertNotNull(bbm);
        assertReport("""
                + Builder tests
                   + Model GeneratorPVFixed GEN instantiation OK
                      'parameterSetId' field is not set, dynamicModelId GEN will be used instead
                """, reportNode);
        BlackBoxModel bbm2 = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .staticId(List.of("ALT_ID", "ALT_ID2", "LOAD"))
                .build();
        assertNotNull(bbm2);
    }

    @Test
    void testAlternativeIdsNotFound() throws IOException {
        ReportNode reportNode = createReportNode();
        Network network = EurostagTutorialExample1Factory.create();
        BlackBoxModel bbm = Objects.requireNonNull(BaseGeneratorBuilder.of(network, "GeneratorPVFixed", reportNode))
                .staticId("ALT_ID", "ALT_ID2")
                .parameterSetId("gen")
                .build();
        assertNull(bbm);
        assertReport("""
                + Builder tests
                   + Model GeneratorPVFixed [ALT_ID, ALT_ID2] instantiation KO
                      None of '[ALT_ID, ALT_ID2]' values from 'staticId' field where found for equipment type(s) GENERATOR
                """, reportNode);
    }

    @Test
    void testAlternativeIdsNotFoundVscChecker() throws IOException {
        ReportNode reportNode = createReportNode();
        Network network = HvdcTestNetwork.createLcc();
        BlackBoxModel bbm = Objects.requireNonNull(HvdcVscBuilder.of(network, "HvdcVsc", reportNode))
                .staticId("ALT_ID", "L")
                .parameterSetId("vsc")
                .build();
        assertNull(bbm);
        assertReport("""
                + Builder tests
                   + Model HvdcVsc [ALT_ID, L] instantiation KO
                      'staticId' field value 'L' should be an HVDC VSC
                      None of '[ALT_ID, L]' values from 'staticId' field where found for equipment type(s) VSC HVDC_LINE
                """, reportNode);
    }

    @Test
    void testAlternativeIdsNotFoundFictitiousChecker() throws IOException {
        ReportNode reportNode = createReportNode();
        Network network = EurostagTutorialExample1Factory.create();
        network.getLoad("LOAD").setFictitious(true);
        BlackBoxModel bbm = Objects.requireNonNull(BaseLoadBuilder.of(network, "LoadAlphaBeta", reportNode))
                .staticId(List.of("ALT_ID", "LOAD"))
                .build();
        assertNull(bbm);
        assertReport("""
                + Builder tests
                   + Model LoadAlphaBeta [ALT_ID, LOAD] instantiation KO
                      'staticId' field value 'LOAD' should not be fictitious
                      None of '[ALT_ID, LOAD]' values from 'staticId' field where found for not fictitious equipment type(s) LOAD
                """, reportNode);
    }

    private ReportNode createReportNode() {
        return ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("testBuilder")
                .build();
    }

    private void assertReport(String expectedReport, ReportNode reportNode) throws IOException {
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(expectedReport, TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
