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
public class MarginCalculationParametersTest {

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
                Arguments.of(-1, 200, 100, 20, 70, 150, "Start time should be zero or positive"),
                Arguments.of(10, 5, 100, 20, 70, 150, "Stop time should be greater than start time"),
                Arguments.of(10, 200, 5, 20, 70, 150, "Margin calculation start time should be between start and stop time"),
                Arguments.of(10, 200, 205, 20, 70, 150, "Margin calculation start time should be between start and stop time"),
                Arguments.of(10, 200, 100, 20, 70, 90, "Contingencies start time should be between margin calculation start time and stop time"),
                Arguments.of(10, 200, 100, 20, 70, 210, "Contingencies start time should be between margin calculation start time and stop time"),
                Arguments.of(10, 200, 100, 5, 70, 150, "Load increase start time should be greater start time"),
                Arguments.of(10, 200, 100, 20, 15, 150, "Load increase stop time should be between load increase start time and margin calculation start time"),
                Arguments.of(10, 200, 100, 20, 110, 150, "Load increase stop time should be between load increase start time and margin calculation start time")
        );
    }
}