/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.results;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class MarginCalculationResult {

    private final List<LoadIncreaseResult> loadIncreaseResults;

    public static MarginCalculationResult empty() {
        return new MarginCalculationResult(Collections.emptyList());
    }

    public MarginCalculationResult(List<LoadIncreaseResult> loadIncreaseResults) {
        this.loadIncreaseResults = Objects.requireNonNull(loadIncreaseResults);
    }

    public List<LoadIncreaseResult> getLoadIncreaseResults() {
        return loadIncreaseResults;
    }
}
