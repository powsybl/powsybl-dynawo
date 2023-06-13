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

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@ExtendWith(CustomParameterResolver.class)
class DisconnectionExceptionXmlTest extends AbstractParametrizedDynamicModelXmlTest {

    @BeforeEach
    void setup(String lib, Function<HvdcLine, BlackBoxModel> constructor) {
        setupNetwork();
        addDynamicModels(constructor);
    }

    protected void setupNetwork() {
        network = HvdcTestNetwork.createVsc();
    }

    protected void addDynamicModels(Function<HvdcLine, BlackBoxModel> constructor) {
        HvdcLine hvdc = network.getHvdcLine("L");
        dynamicModels.add(constructor.apply(hvdc));
        eventModels.add(new EventHvdcDisconnection(hvdc, 1, true, false));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideModels")
    void disconnectionOnDanglingSide(String lib, Function<HvdcLine, BlackBoxModel> constructor) {
        Exception e = assertThrows(PowsyblException.class, this::setupDynawaltzContext);
        assertEquals("Equipment " + lib + " side 1 is dangling and can't be disconnected with an event", e.getMessage());
    }

    private static Stream<Arguments> provideModels() {
        return Stream.of(
                Arguments.of("HvdcPVDangling", (Function<HvdcLine, BlackBoxModel>) hvdc -> new HvdcPvDangling("BBM_L", hvdc, "hvdc", "HvdcPVDangling", Side.ONE)),
                Arguments.of("HvdcVSCDanglingUdc", (Function<HvdcLine, BlackBoxModel>) hvdc -> new HvdcVscDangling("BBM_L", hvdc, "hvdc", "HvdcVSCDanglingUdc", Side.ONE))
        );
    }
}
