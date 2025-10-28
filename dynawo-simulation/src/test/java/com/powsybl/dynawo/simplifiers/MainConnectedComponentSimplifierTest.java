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
import com.powsybl.dynawo.models.buses.InfiniteBusBuilder;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TopologyKind;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
//TODO add remaining cases
class MainConnectedComponentSimplifierTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideEquipment")
    void testMainConnectedSimplifier(Network network, Consumer<Network> equipmentConfig, Function< Network, BlackBoxModel> equipmentConstructor, String expectedReport) throws IOException {
        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("simplifierTest")
                .build();
        equipmentConfig.accept(network);
        List<BlackBoxModel> dynamicModels = List.of(equipmentConstructor.apply(network));
        MainConnectedComponentSimplifier simplifier = new MainConnectedComponentSimplifier();
        List<BlackBoxModel> filteredModels = dynamicModels.stream().filter(simplifier.getModelRemovalPredicate(reportNode)).toList();

        assertEquals(0, filteredModels.size());
        assertReport(expectedReport, reportNode);
    }

    private static Stream<Arguments> provideEquipment() {
        return Stream.of(
                Arguments.of(createSmallHvdcNetwork(),
                        (Consumer<Network>) n -> { },
                        (Function<Network, BlackBoxModel>) n -> InfiniteBusBuilder.of(n, "InfiniteBus")
                                .staticId("Bus2")
                                .parameterSetId("Bus")
                                .build(),
                        """
                        + Simplifier test
                           + Main connected component equipment filter
                              Equipment Bus2 is not in main connected component, the model InfiniteBus Bus2 will be skipped
                        """)
        );
    }

    private void assertReport(String expectedReport, ReportNode reportNode) throws IOException {
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(expectedReport, TestUtil.normalizeLineSeparator(sw.toString()));
    }

    private static Network createSmallHvdcNetwork() {

        final var network = Network.create("Test", "test");
        final var voltageLevel1 = network.newVoltageLevel()
                .setId("voltageLevel1")
                .setNominalV(400.0d)
                .setTopologyKind(TopologyKind.BUS_BREAKER)
                .add();
        final var bus1 = voltageLevel1.getBusBreakerView().newBus()
                .setId("Bus1")
                .add();
        final var voltageLevel2 = network.newVoltageLevel()
                .setId("voltageLevel2")
                .setNominalV(400.0d)
                .setTopologyKind(TopologyKind.BUS_BREAKER)
                .add();
        final var bus2 = voltageLevel2.getBusBreakerView().newBus()
                .setId("Bus2")
                .add();
        voltageLevel1.newLccConverterStation()
                .setId("Lcc1")
                .setBus(bus1.getId())
                .setConnectableBus(bus1.getId())
                .setPowerFactor(0.95f)
                .setLossFactor(0.99f)
                .add();
        voltageLevel2.newLccConverterStation()
                .setId("Lcc2")
                .setBus(bus2.getId())
                .setConnectableBus(bus2.getId())
                .setPowerFactor(0.95f)
                .setLossFactor(0.99f)
                .add();
        network.newHvdcLine()
                .setId("hvdcLine")
                .setR(1)
                .setNominalV(300)
                .setConverterStationId1("Lcc1")
                .setConverterStationId2("Lcc2")
                .setMaxP(2000)
                .setActivePowerSetpoint(50)
                .setConvertersMode(HvdcLine.ConvertersMode.SIDE_1_RECTIFIER_SIDE_2_INVERTER)
                .add();

        network.getLccConverterStation("Lcc1").getTerminal().disconnect();
        network.getLccConverterStation("Lcc2").getTerminal().disconnect();
        return network;
    }
}
