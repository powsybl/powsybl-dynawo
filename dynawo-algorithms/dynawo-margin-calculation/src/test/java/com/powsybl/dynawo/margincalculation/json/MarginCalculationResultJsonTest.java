/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.json;

import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynawo.contingency.results.FailedCriterion;
import com.powsybl.dynawo.contingency.results.ScenarioResult;
import com.powsybl.dynawo.contingency.results.Status;
import com.powsybl.dynawo.margincalculation.results.LoadIncreaseResult;
import com.powsybl.dynawo.margincalculation.results.MarginCalculationResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class MarginCalculationResultJsonTest extends AbstractSerDeTest {

    private static MarginCalculationResult create() {
        return new MarginCalculationResult(List.of(
                new LoadIncreaseResult(100.0,
                        Status.CRITERIA_NON_RESPECTED,
                        Collections.emptyList(),
                        Collections.singletonList(new FailedCriterion("Failed crit1", 23.2))),
                new LoadIncreaseResult(75.0,
                        Status.DIVERGENCE,
                        List.of(
                                new ScenarioResult("cont1", Status.CONVERGENCE),
                                new ScenarioResult("cont2", Status.CRITERIA_NON_RESPECTED,
                                        Collections.singletonList(new FailedCriterion("Failed crit", 3.3)))
                        )),
                new LoadIncreaseResult(50.0, Status.CONVERGENCE)));
    }

    @Test
    void roundTripTest() throws IOException {
        roundTripTest(create(), MarginCalculationResultSerializer::write, MarginCalculationResultDeserializer::read, "/MarginCalculationResult.json");
    }

    @Test
    void handleErrorTest() throws IOException {
        try (var is = getClass().getResourceAsStream("/MarginCalculationResultError.json")) {
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> MarginCalculationResultDeserializer.read(is));
            assertEquals("Unexpected field: err", e.getMessage());
        }
    }

}
