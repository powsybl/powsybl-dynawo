/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawo.algorithms;

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
    void testExceptions(String staticId, double startTime, double stopTime, double rPu, double xPu, String message) {
        NodeFaultEventData.Builder builder = NodeFaultEventData.builder()
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
        return Stream.of(
                Arguments.of("staticId", 0, 10, -1, 1, "rPu (%.2f) should be zero or positive".formatted(-1f)),
                Arguments.of("staticId", 0, 10, 1, -1, "xPu (%.2f) should be zero or positive".formatted(-1f)),
                Arguments.of("staticId", -1, 10, 1, 1, "Start time (%.2f) should be zero or positive".formatted(-1f)),
                Arguments.of("staticId", 10, 5, 1, 1, "Stop time (%.2f) should be greater than start time (%.2f)".formatted(5f, 10f))
        );
    }
}

