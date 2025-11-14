/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.results;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationResults {
    private final List<CriticalTimeCalculationResult> criticalTimeCalculationResults;

    public static CriticalTimeCalculationResults empty() {
        return new CriticalTimeCalculationResults(Collections.emptyList());
    }

    public CriticalTimeCalculationResults(List<CriticalTimeCalculationResult> criticalTimeCalculationResults) {
        this.criticalTimeCalculationResults = Objects.requireNonNull(criticalTimeCalculationResults);
    }

    public List<CriticalTimeCalculationResult> getCriticalTimeCalculationResults() {
        return criticalTimeCalculationResults;
    }
}
