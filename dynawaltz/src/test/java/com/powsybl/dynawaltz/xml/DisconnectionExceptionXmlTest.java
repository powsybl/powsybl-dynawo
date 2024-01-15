/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.builders.EventModelsBuilderUtils;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawaltz.models.hvdc.HvdcVscBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@ExtendWith(CustomParameterResolver.class)
class DisconnectionExceptionXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    @BeforeEach
    void setup(String exception, TwoSides side, BiFunction<Network, TwoSides, BlackBoxModel> constructor) {
        setupNetwork();
        addDynamicModels(side, constructor);
    }

    protected void setupNetwork() {
        network = HvdcTestNetwork.createVsc();
    }

    protected void addDynamicModels(TwoSides side, BiFunction<Network, TwoSides, BlackBoxModel> constructor) {
        dynamicModels.add(constructor.apply(network, side));
        eventModels.add(EventModelsBuilderUtils.newEventDisconnectionBuilder(network)
                .staticId("L")
                .startTime(1)
                .disconnectOnly(side)
                .build());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideModels")
    void disconnectionOnDanglingSide(String exception, TwoSides side, BiFunction<Network, TwoSides, BlackBoxModel> constructor) {
        Exception e = assertThrows(PowsyblException.class, this::setupDynawaltzContext);
        assertEquals(exception, e.getMessage());
    }

    private static Stream<Arguments> provideModels() {
        return Stream.of(
                Arguments.of("Equipment HvdcPVDangling side 1 is dangling and can't be disconnected with an event",
                        TwoSides.ONE,
                        (BiFunction<Network, TwoSides, BlackBoxModel>) (network, side) -> HvdcPBuilder.of(network, "HvdcPVDangling")
                                .dynamicModelId("BBM_L")
                                .staticId("L")
                                .parameterSetId("hvdc")
                                .dangling(side)
                                .build()),
                Arguments.of("Equipment HvdcVSCDanglingUdc side 2 is dangling and can't be disconnected with an event",
                        TwoSides.TWO,
                        (BiFunction<Network, TwoSides, BlackBoxModel>) (network, side) -> HvdcVscBuilder.of(network, "HvdcVSCDanglingUdc")
                                .dynamicModelId("BBM_L")
                                .staticId("L")
                                .parameterSetId("hvdc")
                                .dangling(side)
                                .build())
        );
    }
}
