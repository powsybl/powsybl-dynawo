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
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicTwoLevelOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawo.models.loads.LoadOneTransformerBuilder;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class EnergizedSimplifierTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideEquipment")
    void testEnergizedSimplifier(Network network, Consumer<Network> equipmentConfig, Function< Network, BlackBoxModel> equipmentConstructor, String expectedReport) throws IOException {
        ReportNode reportNode = ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME,
                        PowsyblTestReportResourceBundle.TEST_BASE_NAME)
                .withMessageTemplate("simplifierTest")
                .build();
        equipmentConfig.accept(network);
        List<BlackBoxModel> dynamicModels = List.of(equipmentConstructor.apply(network));
        EnergizedSimplifier simplifier = new EnergizedSimplifier();
        List<BlackBoxModel> filteredModels = dynamicModels.stream().filter(simplifier.getModelRemovalPredicate(reportNode)).toList();

        assertTrue(filteredModels.isEmpty());
        assertReport(expectedReport, reportNode);
    }

    @Test
    void testNoFilterEnergizedSimplifier() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        List<BlackBoxModel> dynamicModels = List.of(
                LoadOneTransformerBuilder.of(network)
                        .staticId("LOAD")
                        .build(),
                TapChangerAutomationSystemBuilder.of(network)
                        .dynamicModelId("TC")
                        .staticId("LOAD")
                        .build());
        EnergizedSimplifier simplifier = new EnergizedSimplifier();
        List<BlackBoxModel> filteredModels = dynamicModels.stream().filter(simplifier.getModelRemovalPredicate(ReportNode.NO_OP)).toList();

        assertEquals(2, filteredModels.size());
    }

    private static Stream<Arguments> provideEquipment() {
        return Stream.of(
                Arguments.of(EurostagTutorialExample1Factory.create(),
                        (Consumer<Network>) n -> n.getGenerator("GEN").getTerminal().disconnect(),
                        (Function<Network, BlackBoxModel>) n -> SynchronousGeneratorBuilder.of(n, "GeneratorSynchronousFourWindings")
                                .staticId("GEN")
                                .parameterSetId("GSFWPR")
                                .build(),
                        """
                        + Simplifier test
                           + Energized model filter
                              Equipment GEN terminal is not connected, model GeneratorSynchronousFourWindings GEN will be skipped
                        """),
                Arguments.of(HvdcTestNetwork.createVsc(),
                        (Consumer<Network>) n -> n.getBusBreakerView().getBus("B1").setV(1.0),
                        (Function<Network, BlackBoxModel>) n -> HvdcPBuilder.of(n, "HvdcPV")
                                .staticId("L")
                                .parameterSetId("hv")
                                .build(),
                        """
                        + Simplifier test
                           + Energized model filter
                              Bus VL2_2 is not energized, model HvdcPVInverted L will be skipped
                        """),
                Arguments.of(HvdcTestNetwork.createVsc(),
                        (Consumer<Network>) n -> { },
                        (Function<Network, BlackBoxModel>) n -> HvdcPBuilder.of(n, "HvdcPVDangling")
                                .staticId("L")
                                .parameterSetId("hv")
                                .build(),
                        """
                        + Simplifier test
                           + Energized model filter
                              Bus B1 is not energized, model HvdcPVDanglingInverted L will be skipped
                        """),
                Arguments.of(EurostagTutorialExample1Factory.create(),
                        (Consumer<Network>) n -> n.getLine("NHV1_NHV2_1").getTerminal1().disconnect(),
                        (Function<Network, BlackBoxModel>) n -> DynamicOverloadManagementSystemBuilder.of(n)
                                .dynamicModelId("OMS")
                                .parameterSetId("oms")
                                .controlledBranch("NHV1_NHV2_1")
                                .iMeasurement("NHV1_NHV2_2")
                                .iMeasurementSide(TwoSides.TWO)
                                .build(),
                        """
                        + Simplifier test
                           + Energized model filter
                              Equipment NHV1_NHV2_1 terminal is not connected, model CurrentLimitAutomaton OMS will be skipped
                        """),
                Arguments.of(EurostagTutorialExample1Factory.create(),
                        (Consumer<Network>) n -> n.getBusBreakerView().getBus("NGEN").setV(1.0),
                        (Function<Network, BlackBoxModel>) n -> DynamicOverloadManagementSystemBuilder.of(n)
                                .dynamicModelId("OMS")
                                .parameterSetId("oms")
                                .controlledBranch("NHV1_NHV2_1")
                                .iMeasurement("NGEN_NHV1")
                                .iMeasurementSide(TwoSides.TWO)
                                .build(),
                        """
                        + Simplifier test
                           + Energized model filter
                              Bus NHV1 is not energized, model CurrentLimitAutomaton OMS will be skipped
                        """),
                Arguments.of(EurostagTutorialExample1Factory.create(),
                        (Consumer<Network>) n -> n.getBusBreakerView().getBus("NGEN").setV(1.0),
                        (Function<Network, BlackBoxModel>) n -> DynamicTwoLevelOverloadManagementSystemBuilder.of(n)
                                .dynamicModelId("OMSTL")
                                .parameterSetId("oms")
                                .controlledBranch("NHV1_NHV2_1")
                                .iMeasurement1("NHV1_NHV2_1")
                                .iMeasurement1Side(TwoSides.ONE)
                                .iMeasurement2("NGEN_NHV1")
                                .iMeasurement2Side(TwoSides.TWO)
                                .build(),
                        """
                        + Simplifier test
                           + Energized model filter
                              Bus NHV1 is not energized, model CurrentLimitAutomatonTwoLevels OMSTL will be skipped
                        """)
        );
    }

    private void assertReport(String expectedReport, ReportNode reportNode) throws IOException {
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(expectedReport, TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
