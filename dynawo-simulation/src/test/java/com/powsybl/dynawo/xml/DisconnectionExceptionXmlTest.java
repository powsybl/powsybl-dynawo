/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.LfResultsUtils;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcVscBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;
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

    private static final DynawoVersion DYNAWO_VERSION = new DynawoVersion(1, 6, 0);

    @BeforeEach
    void setup(String exception, TwoSides side, BiFunction<Network, TwoSides, BlackBoxModel> constructor) {
        setupNetwork();
        addDynamicModels(side, constructor);
    }

    protected void setupNetwork() {
        network = LfResultsUtils.createHvdcTestNetworkVscWithLFResults();
    }

    protected void addDynamicModels(TwoSides side, BiFunction<Network, TwoSides, BlackBoxModel> constructor) {
        dynamicModels.add(constructor.apply(network, side));
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("L")
                .startTime(1)
                .disconnectOnly(side)
                .build());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideModels")
    void disconnectionOnDanglingSide(String exception, TwoSides side, BiFunction<Network, TwoSides, BlackBoxModel> constructor) {
        DynawoSimulationContext.Builder builder = setupDynawoContextBuilder().currentVersion(DYNAWO_VERSION);
        Exception e = assertThrows(PowsyblException.class, builder::build);
        assertEquals(exception, e.getMessage());
    }

    private static Stream<Arguments> provideModels() {
        return Stream.of(
                Arguments.of("Equipment HvdcPVDangling side 2 is dangling and can't be disconnected with an event",
                        TwoSides.TWO,
                        (BiFunction<Network, TwoSides, BlackBoxModel>) (network, side) -> HvdcPBuilder.of(network, "HvdcPVDangling")
                                .staticId("L")
                                .parameterSetId("hvdc")
                                .dangling(side)
                                .build()),
                Arguments.of("Equipment HvdcVscDanglingUDc side 2 is dangling and can't be disconnected with an event",
                        TwoSides.TWO,
                        (BiFunction<Network, TwoSides, BlackBoxModel>) (network, side) -> HvdcVscBuilder.of(network, "HvdcVscDanglingUDc")
                                .staticId("L")
                                .parameterSetId("hvdc")
                                .dangling(side)
                                .build())
        );
    }
}
