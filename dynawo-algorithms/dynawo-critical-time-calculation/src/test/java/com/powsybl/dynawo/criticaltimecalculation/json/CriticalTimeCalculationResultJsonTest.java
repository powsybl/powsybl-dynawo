/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.json;

import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResult;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResults;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.powsybl.dynawo.contingency.results.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationResultJsonTest extends AbstractSerDeTest {

    private static CriticalTimeCalculationResults create() {
        return new CriticalTimeCalculationResults(List.of(
                new CriticalTimeCalculationResult("MyFirstScenario", RESULT_FOUND, 1),
                new CriticalTimeCalculationResult("MySecondScenario", CT_BELOW_MIN_BOUND),
                new CriticalTimeCalculationResult("MyThirdScenario", CT_ABOVE_MAX_BOUND))
        );
    }

    @Test
    void roundTripTest() throws IOException {
        roundTripTest(create(), CriticalTimeCalculationResultsSerializer::write, CriticalTimeCalculationResultsDeserializer::read, "/CriticalTimeCalculationResult.json");
    }

    @Test
    void handleErrorTest() throws IOException {
        try (var is = getClass().getResourceAsStream("/CriticalTimeCalculationResultError.json")) {
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> CriticalTimeCalculationResultsDeserializer.read(is));
            assertEquals("Unexpected field: err", e.getMessage());
        }
    }
}
