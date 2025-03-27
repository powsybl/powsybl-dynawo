/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class MarginCalculationParametersTest {

    @ParameterizedTest(name = "{6}")
    @MethodSource("provideMarginCalculationTimes")
    void testExceptions(double startTime, double stopTime, double marginCalculationStartTime, double loadIncreaseStartTime,
                        double loadIncreaseStopTime, double contingenciesStartTime, String message) {
        MarginCalculationParameters.Builder builder = MarginCalculationParameters.builder()
                .setStartTime(startTime)
                .setStopTime(stopTime)
                .setMarginCalculationStartTime(marginCalculationStartTime)
                .setLoadIncreaseStartTime(loadIncreaseStartTime)
                .setLoadIncreaseStopTime(loadIncreaseStopTime)
                .setContingenciesStartTime(contingenciesStartTime);
        assertThatThrownBy(builder::build)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(message);
    }

    private static Stream<Arguments> provideMarginCalculationTimes() {
        return Stream.of(
                Arguments.of(-1, 200, 100, 20, 70, 150, "Start time (%.2f) should be zero or positive".formatted(-1f)),
                Arguments.of(10, 5, 100, 20, 70, 150, "Stop time (%.2f) should be greater than start time (%.2f)".formatted(5f, 10f)),
                Arguments.of(10, 200, 5, 20, 70, 150, "Margin calculation start time (%.2f) should be between start (%.2f) and stop time (%.2f)".formatted(5f, 10f, 200f)),
                Arguments.of(10, 200, 205, 20, 70, 150, "Margin calculation start time (%.2f) should be between start (%.2f) and stop time (%.2f)".formatted(205f, 10f, 200f)),
                Arguments.of(10, 200, 100, 20, 70, 90, "Contingencies start time (%.2f) should be between margin calculation start time (%.2f) and stop time (%.2f)".formatted(90f, 100f, 200f)),
                Arguments.of(10, 200, 100, 20, 70, 210, "Contingencies start time (%.2f) should be between margin calculation start time (%.2f) and stop time (%.2f)".formatted(210f, 100f, 200f)),
                Arguments.of(10, 200, 100, 5, 70, 150, "Load increase start time (%.2f) should be greater than start time (%.2f)".formatted(5f, 10f)),
                Arguments.of(10, 200, 100, 20, 15, 150, "Load increase stop time (%.2f) should be between load increase start time (%.2f) and margin calculation start time (%.2f)".formatted(15f, 20f, 100f)),
                Arguments.of(10, 200, 100, 20, 110, 150, "Load increase stop time (%.2f) should be between load increase start time (%.2f) and margin calculation start time (%.2f)".formatted(110f, 20f, 100f))
        );
    }
}
