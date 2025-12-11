/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.simplifiers;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.PowsyblTestReportResourceBundle;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawo.models.loads.LoadOneTransformerBuilder;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class MainConnectedComponentSimplifierTest {

    @Test
    void testMainConnectedSimplifier() throws IOException {
        Network network = createSubComponentEurostag();
        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("simplifierTest")
                .build();
        List<BlackBoxModel> dynamicModels = List.of(
                LoadOneTransformerBuilder.of(network)
                        .staticId("LOAD")
                        .build(),
                TapChangerAutomationSystemBuilder.of(network)
                        .dynamicModelId("TC")
                        .staticId("LOAD")
                        .build(),
                SynchronizedGeneratorBuilder.of(network, "GeneratorPQ")
                        .staticId("GEN3")
                        .build(),
                HvdcPBuilder.of(network, "HvdcPV")
                        .staticId("L")
                        .build()
        );
        MainConnectedComponentSimplifier simplifier = new MainConnectedComponentSimplifier();
        List<BlackBoxModel> filteredModels = dynamicModels.stream().filter(simplifier.getModelRemovalPredicate(reportNode)).toList();

        String expectedReport = """
            + Simplifier test
               + Main connected component equipment filter
                  Equipment GEN3 is not in main connected component, the model GeneratorPQ GEN3 will be skipped
                  Equipment L is not in main connected component, the model HvdcPV L will be skipped
            """;

        assertEquals(2, filteredModels.size());
        assertReport(expectedReport, reportNode);
    }

    private void assertReport(String expectedReport, ReportNode reportNode) throws IOException {
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(expectedReport, TestUtil.normalizeLineSeparator(sw.toString()));
    }

    private static Network createSubComponentEurostag() {
        Network network = EurostagTutorialExample1Factory.create();
        Substation st = network.newSubstation()
                .setId("P3")
                .setCountry(Country.FR)
                .setTso("RTE")
                .setGeographicalTags("A")
            .add();
        VoltageLevel vlSub = st.newVoltageLevel()
                .setId("VLSUB")
                .setNominalV(24.0)
                .setTopologyKind(TopologyKind.BUS_BREAKER)
            .add();
        VoltageLevel vlSub2 = st.newVoltageLevel()
                .setId("VLSUB2")
                .setNominalV(24.0)
                .setTopologyKind(TopologyKind.BUS_BREAKER)
                .add();
        vlSub.getBusBreakerView().newBus()
                .setId("BUS")
                .add();
        vlSub2.getBusBreakerView().newBus()
                .setId("BUS2")
                .add();
        vlSub.newGenerator()
                .setId("GEN3")
                .setBus("BUS")
                .setConnectableBus("BUS")
                .setMinP(-9999.99)
                .setMaxP(9999.99)
                .setVoltageRegulatorOn(true)
                .setTargetV(24.5)
                .setTargetP(607.0)
                .setTargetQ(301.0)
            .add();
        vlSub.newLccConverterStation()
                .setId("LCC1")
                .setBus("BUS")
                .setConnectableBus("BUS")
                .setPowerFactor(0.95f)
                .setLossFactor(0.99f)
                .add();
        vlSub2.newLccConverterStation()
                .setId("LCC2")
                .setBus("BUS2")
                .setConnectableBus("BUS2")
                .setPowerFactor(0.95f)
                .setLossFactor(0.99f)
                .add();
        network.newHvdcLine()
                .setId("L")
                .setR(1)
                .setNominalV(300)
                .setConverterStationId1("LCC1")
                .setConverterStationId2("LCC2")
                .setMaxP(2000)
                .setActivePowerSetpoint(50)
                .setConvertersMode(HvdcLine.ConvertersMode.SIDE_1_RECTIFIER_SIDE_2_INVERTER)
                .add();
        return network;
    }
}
