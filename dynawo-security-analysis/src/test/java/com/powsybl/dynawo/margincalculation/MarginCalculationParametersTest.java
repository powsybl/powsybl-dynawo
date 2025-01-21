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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Exception e = assertThrows(IllegalStateException.class, builder::build);
        assertEquals(message, e.getMessage());
    }

    private static Stream<Arguments> provideMarginCalculationTimes() {
        return Stream.of(
                Arguments.of(-1, 200, 100, 20, 70, 150, "Start time (-1,00) should be zero or positive"),
                Arguments.of(10, 5, 100, 20, 70, 150, "Stop time (5,00) should be greater than start time (10,00)"),
                Arguments.of(10, 200, 5, 20, 70, 150, "Margin calculation start time (5,00) should be between start (10,00) and stop time (200,00)"),
                Arguments.of(10, 200, 205, 20, 70, 150, "Margin calculation start time (205,00) should be between start (10,00) and stop time (200,00)"),
                Arguments.of(10, 200, 100, 20, 70, 90, "Contingencies start time (90,00) should be between margin calculation start time (100,00) and stop time (200,00)"),
                Arguments.of(10, 200, 100, 20, 70, 210, "Contingencies start time (210,00) should be between margin calculation start time (100,00) and stop time (200,00)"),
                Arguments.of(10, 200, 100, 5, 70, 150, "Load increase start time (5,00) should be greater than start time (10,00)"),
                Arguments.of(10, 200, 100, 20, 15, 150, "Load increase stop time (15,00) should be between load increase start time (20,00) and margin calculation start time (100,00)"),
                Arguments.of(10, 200, 100, 20, 110, 150, "Load increase stop time (110,00) should be between load increase start time (20,00) and margin calculation start time (100,00)")
        );
    }
}
