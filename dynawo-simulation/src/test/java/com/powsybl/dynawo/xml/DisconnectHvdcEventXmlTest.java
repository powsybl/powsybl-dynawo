/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcVscBuilder;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;
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
class DisconnectHvdcEventXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    private static final String HVDC_NAME = "L";
    private static final String DYN_HVDC_NAME = "BBM_HVDC";

    @BeforeEach
    void setup(String dydName, Function< Network, BlackBoxModel> hvdcConstructor, Function< Network, BlackBoxModel> disconnectConstructor) {
        setupNetwork();
        addDynamicModels(hvdcConstructor, disconnectConstructor);
        setupDynawoContext();
    }

    protected void setupNetwork() {
        network = HvdcTestNetwork.createVsc();
    }

    protected void addDynamicModels(Function< Network, BlackBoxModel> hvdcConstructor, Function< Network, BlackBoxModel> disconnectConstructor) {
        if (hvdcConstructor != null) {
            dynamicModels.add(hvdcConstructor.apply(network));
        }
        eventModels.add(disconnectConstructor.apply(network));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideModels")
    void writeModel(String dydName, Function< Network, BlackBoxModel> hvdcConstructor, Function< Network, BlackBoxModel> disconnectConstructor) throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", dydName, tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "disconnect_hvdc_par.xml", tmpDir.resolve(context.getSimulationParFile()));

    }

    private static Stream<Arguments> provideModels() {
        return Stream.of(
                Arguments.of("disconnect_default_hvdc_dyd.xml",
                        null,
                        (Function<Network, BlackBoxModel>) n -> EventDisconnectionBuilder.of(n)
                                .staticId(HVDC_NAME)
                                .startTime(1)
                                .build()),
                Arguments.of("disconnect_hvdc_pv_dyd.xml",
                        (Function<Network, BlackBoxModel>) n -> HvdcPBuilder.of(n, "HvdcPV")
                                .dynamicModelId(DYN_HVDC_NAME)
                                .staticId(HVDC_NAME)
                                .parameterSetId("hvdc")
                                .build(),
                        (Function<Network, BlackBoxModel>) n -> EventDisconnectionBuilder.of(n)
                                .staticId(HVDC_NAME)
                                .startTime(1)
                                .disconnectOnly(TwoSides.ONE)
                                .build()),
                Arguments.of("disconnect_hvdc_vsc_dyd.xml",
                        (Function<Network, BlackBoxModel>) n -> HvdcVscBuilder.of(n, "HvdcVsc")
                                .dynamicModelId(DYN_HVDC_NAME)
                                .staticId(HVDC_NAME)
                                .parameterSetId("hvdc")
                                .build(),
                        (Function<Network, BlackBoxModel>) n -> EventDisconnectionBuilder.of(n)
                                .staticId(HVDC_NAME)
                                .startTime(1)
                                .disconnectOnly(TwoSides.TWO)
                                .build()),
                Arguments.of("disconnect_hvdc_pv_dangling_dyd.xml",
                        (Function<Network, BlackBoxModel>) n -> HvdcPBuilder.of(n, "HvdcPVDangling")
                                .dynamicModelId(DYN_HVDC_NAME)
                                .staticId(HVDC_NAME)
                                .parameterSetId("hvdc")
                                .dangling(TwoSides.TWO)
                                .build(),
                        (Function<Network, BlackBoxModel>) n -> EventDisconnectionBuilder.of(n)
                                .staticId(HVDC_NAME)
                                .startTime(1)
                                .disconnectOnly(TwoSides.ONE)
                                .build()),
                Arguments.of("disconnect_hvdc_vsc_dangling_dyd.xml",
                        (Function<Network, BlackBoxModel>) n -> HvdcVscBuilder.of(n, "HvdcVscDanglingUdc")
                                .dynamicModelId(DYN_HVDC_NAME)
                                .staticId(HVDC_NAME)
                                .parameterSetId("hvdc")
                                .dangling(TwoSides.TWO)
                                .build(),
                        (Function<Network, BlackBoxModel>) n -> EventDisconnectionBuilder.of(n)
                                .staticId(HVDC_NAME)
                                .startTime(1)
                                .disconnectOnly(TwoSides.ONE)
                                .build())
        );
    }
}
