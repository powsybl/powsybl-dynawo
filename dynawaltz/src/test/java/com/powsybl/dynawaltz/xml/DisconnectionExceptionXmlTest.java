/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.events.EventHvdcDisconnection;
import com.powsybl.dynawaltz.models.hvdc.HvdcPvDangling;
import com.powsybl.dynawaltz.models.hvdc.HvdcVscDangling;
import com.powsybl.iidm.network.HvdcLine;
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
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@ExtendWith(CustomParameterResolver.class)
class DisconnectionExceptionXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    @BeforeEach
    void setup(String exception, Side side, BiFunction<HvdcLine, Side, BlackBoxModel> constructor) {
        setupNetwork();
        addDynamicModels(side, constructor);
    }

    protected void setupNetwork() {
        network = HvdcTestNetwork.createVsc();
    }

    protected void addDynamicModels(Side side, BiFunction<HvdcLine, Side, BlackBoxModel> constructor) {
        HvdcLine hvdc = network.getHvdcLine("L");
        dynamicModels.add(constructor.apply(hvdc, side));
        boolean disconnectOrigin = Side.ONE == side;
        eventModels.add(new EventHvdcDisconnection(hvdc, 1, disconnectOrigin, !disconnectOrigin));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideModels")
    void disconnectionOnDanglingSide(String exception, Side side, BiFunction<HvdcLine, Side, BlackBoxModel> constructor) {
        Exception e = assertThrows(PowsyblException.class, this::setupDynawaltzContext);
        assertEquals(exception, e.getMessage());
    }

    private static Stream<Arguments> provideModels() {
        return Stream.of(
                Arguments.of("Equipment HvdcPVDangling side 1 is dangling and can't be disconnected with an event",
                        Side.ONE,
                        (BiFunction<HvdcLine, Side, BlackBoxModel>) (hvdc, side) -> new HvdcPvDangling("BBM_L", hvdc, "hvdc", "HvdcPVDangling", side)),
                Arguments.of("Equipment HvdcVSCDanglingUdc side 2 is dangling and can't be disconnected with an event",
                        Side.TWO,
                        (BiFunction<HvdcLine, Side, BlackBoxModel>) (hvdc, side) -> new HvdcVscDangling("BBM_L", hvdc, "hvdc", "HvdcVSCDanglingUdc", side))
        );
    }
}
