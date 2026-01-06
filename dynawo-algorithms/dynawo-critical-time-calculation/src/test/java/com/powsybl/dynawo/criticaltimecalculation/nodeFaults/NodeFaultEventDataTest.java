/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawo.criticaltimecalculation.nodeFaults;

import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultEventData;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public class NodeFaultEventDataTest {
    @ParameterizedTest(name = "{6}")
    @MethodSource("provideNodeFaultEventDataTimes")
    void testExceptions(Network network, String staticId, double startTime, double stopTime, double rPu, double xPu, String message) {
        NodeFaultEventData.Builder builder = NodeFaultEventData.builder(network)
                .setStaticId(staticId)
                .setFaultStartTime(startTime)
                .setFaultStopTime(stopTime)
                .setFaultRPu(rPu)
                .setFaultXPu(xPu);
        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(message);
    }

    private static Stream<Arguments> provideNodeFaultEventDataTimes() {
        Network network = EurostagTutorialExample1Factory.create();
        return Stream.of(
                Arguments.of(network, "GEN4", 0, 10, -1, 1, "Static Id 'null' was not found."),
                Arguments.of(network, "GEN", 0, 10, -1, 1, "rPu (%.2f) should be zero or positive".formatted(-1f)),
                Arguments.of(network, "GEN", 0, 10, 1, -1, "xPu (%.2f) should be zero or positive".formatted(-1f)),
                Arguments.of(network, "GEN", -1, 10, 1, 1, "Start time (%.2f) should be zero or positive".formatted(-1f)),
                Arguments.of(network, "GEN", 10, 5, 1, 1, "Stop time (%.2f) should be greater than start time (%.2f)".formatted(5f, 10f))
        );
    }
}

