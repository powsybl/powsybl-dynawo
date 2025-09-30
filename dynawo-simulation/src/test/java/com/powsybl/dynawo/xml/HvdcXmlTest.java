/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcVscBuilder;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@ExtendWith(CustomParameterResolver.class)
class HvdcXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String HVDC_NAME = "L";
    private static final DynawoVersion DYNAWO_VERSION = new DynawoVersion(1, 6, 0);

    @BeforeEach
    void setup(String dydName, HvdcLine.ConvertersMode convertersMode, Function< Network, BlackBoxModel> constructor) {
        setupNetwork(convertersMode);
        addDynamicModels(constructor);
        context = setupDynawoContextBuilder()
                .currentVersion(DYNAWO_VERSION)
                .build();
    }

    protected void setupNetwork(HvdcLine.ConvertersMode convertersMode) {
        network = HvdcTestNetwork.createVsc();
        network.getHvdcLine(HVDC_NAME).setConvertersMode(convertersMode);
    }

    protected void addDynamicModels(Function< Network, BlackBoxModel> constructor) {
        dynamicModels.add(constructor.apply(network));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideHvdc")
    void writeHvdcModel(String dydName, HvdcLine.ConvertersMode convertersMode,
                        Function< Network, BlackBoxModel> constructor) throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", dydName, tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }

    private static Stream<Arguments> provideHvdc() {
        return Stream.of(
                Arguments.of("hvdc_p_dyd.xml", HvdcLine.ConvertersMode.SIDE_1_RECTIFIER_SIDE_2_INVERTER,
                        (Function<Network, BlackBoxModel>) n -> HvdcPBuilder.of(n, "HvdcPV")
                            .staticId(HVDC_NAME)
                            .parameterSetId("hv")
                            .build()),
                Arguments.of("hvdc_vsc_dyd.xml", HvdcLine.ConvertersMode.SIDE_1_RECTIFIER_SIDE_2_INVERTER,
                        (Function<Network, BlackBoxModel>) n -> HvdcVscBuilder.of(n, "HvdcVsc")
                            .staticId(HVDC_NAME)
                            .parameterSetId("hv")
                            .build()),
                Arguments.of("hvdc_p_dangling_dyd.xml", HvdcLine.ConvertersMode.SIDE_1_RECTIFIER_SIDE_2_INVERTER,
                        (Function<Network, BlackBoxModel>) n -> HvdcPBuilder.of(n, "HvdcPVDangling")
                            .staticId(HVDC_NAME)
                            .parameterSetId("hv")
                            .build()),
                Arguments.of("hvdc_vsc_dangling_dyd.xml", HvdcLine.ConvertersMode.SIDE_1_RECTIFIER_SIDE_2_INVERTER,
                        (Function<Network, BlackBoxModel>) n -> HvdcVscBuilder.of(n, "HvdcVscDanglingP")
                            .staticId(HVDC_NAME)
                            .parameterSetId("hv")
                            .build()),
                Arguments.of("hvdc_p_inverted_dyd.xml", HvdcLine.ConvertersMode.SIDE_1_INVERTER_SIDE_2_RECTIFIER,
                        (Function<Network, BlackBoxModel>) n -> HvdcPBuilder.of(n, "HvdcPV")
                                .staticId(HVDC_NAME)
                                .parameterSetId("hv")
                                .build()),
                Arguments.of("hvdc_vsc_inverted_dyd.xml", HvdcLine.ConvertersMode.SIDE_1_INVERTER_SIDE_2_RECTIFIER,
                        (Function<Network, BlackBoxModel>) n -> HvdcVscBuilder.of(n, "HvdcVsc")
                                .staticId(HVDC_NAME)
                                .parameterSetId("hv")
                                .build()),
                Arguments.of("hvdc_p_dangling_inverted_dyd.xml", HvdcLine.ConvertersMode.SIDE_1_INVERTER_SIDE_2_RECTIFIER,
                        (Function<Network, BlackBoxModel>) n -> HvdcPBuilder.of(n, "HvdcPVDangling")
                                .staticId(HVDC_NAME)
                                .parameterSetId("hv")
                                .build()),
                Arguments.of("hvdc_vsc_dangling_inverted_dyd.xml", HvdcLine.ConvertersMode.SIDE_1_INVERTER_SIDE_2_RECTIFIER,
                        (Function<Network, BlackBoxModel>) n -> HvdcVscBuilder.of(n, "HvdcVscDanglingP")
                                .staticId(HVDC_NAME)
                                .parameterSetId("hv")
                                .build())
        );
    }
}
