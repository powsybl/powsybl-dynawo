/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation;

import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationParameters.Mode;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationParametersTest {

    @ParameterizedTest(name = "{6}")
    @MethodSource("provideCriticalTimeCalculationTimes")
    void testExceptions(double startTime, double stopTime, double minValue, double maxValue, double accuracy,
                        String elementId, Mode mode, String message) {
        CriticalTimeCalculationParameters.Builder builder = CriticalTimeCalculationParameters.builder()
                .setStartTime(startTime)
                .setStopTime(stopTime)
                .setMinValue(minValue)
                .setMaxValue(maxValue)
                .setAccuracy(accuracy)
                .setElementId(elementId)
                .setMode(mode);
        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(message);
    }

    private static Stream<Arguments> provideCriticalTimeCalculationTimes() {
        return Stream.of(
                Arguments.of(-1, 200, 1, 3, 0.001, "ElementId", Mode.SIMPLE, "Start time (%.2f) should be zero or positive".formatted(-1f)),
                Arguments.of(10, 5, 1, 3, 0.001, "ElementId", Mode.SIMPLE, "Stop time (%.2f) should be greater than start time (%.2f)".formatted(5f, 10f)),
                Arguments.of(10, 200, 3, 1, 0.001, "ElementId", Mode.SIMPLE, "Gap between minValue (%.2f) and maxValue (%.2f) must be at least two times the accuracy with min < max".formatted(3f, 1f)),
                Arguments.of(10, 200, 1, 2, 1, "ElementId", Mode.SIMPLE, "Gap between minValue (%.2f) and maxValue (%.2f) must be at least two times the accuracy with min < max".formatted(1f, 2f)),
                Arguments.of(10, 200, 1, 3, -1, "ElementId", Mode.SIMPLE, "Accuracy should be a number above 0 (found : (%.2f))".formatted(-1f))
        );
    }
}
