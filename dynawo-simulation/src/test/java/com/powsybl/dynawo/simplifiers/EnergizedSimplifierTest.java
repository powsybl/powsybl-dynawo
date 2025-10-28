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
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawo.models.transformers.TransformerFixedRatioBuilder;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
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

        assertEquals(0, filteredModels.size());
        assertReport(expectedReport, reportNode);
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
                Arguments.of(EurostagTutorialExample1Factory.create(),
                        (Consumer<Network>) n -> { },
                        (Function<Network, BlackBoxModel>) n -> TransformerFixedRatioBuilder.of(n, "TransformerFixedRatio")
                                .staticId("NGEN_NHV1")
                                .parameterSetId("TFR")
                                .build(),
                        """
                        + Simplifier test
                           + Energized model filter
                              Equipment NGEN_NHV1 is not energized, model TransformerFixedRatio NGEN_NHV1 will be skipped
                        """),
                Arguments.of(EurostagTutorialExample1Factory.create(),
                        (Consumer<Network>) n -> n.getBusBreakerView().getBus("NGEN").setV(1.0),
                        (Function<Network, BlackBoxModel>) n -> TransformerFixedRatioBuilder.of(n, "TransformerFixedRatio")
                                .staticId("NGEN_NHV1")
                                .parameterSetId("TFR")
                                .build(),
                        """
                        + Simplifier test
                           + Energized model filter
                              Equipment NGEN_NHV1 is not energized, model TransformerFixedRatio NGEN_NHV1 will be skipped
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
                              Equipment L is not energized, model HvdcPVInverted L will be skipped
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
                              Equipment L is not energized, model HvdcPVDanglingInverted L will be skipped
                        """)
        );
    }

    private void assertReport(String expectedReport, ReportNode reportNode) throws IOException {
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(expectedReport, TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
