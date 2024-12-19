/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.results;

import com.powsybl.dynaflow.results.FailedCriterion;
import com.powsybl.dynaflow.results.ScenarioResult;
import com.powsybl.dynaflow.results.Status;

import java.util.Collections;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record LoadIncreaseResult(double loadLevel, Status status,
                                 List<ScenarioResult> scenarioResults, List<FailedCriterion> failedCriteria) {

    public LoadIncreaseResult(double loadLevel, Status status) {
        this(loadLevel, status, Collections.emptyList(), Collections.emptyList());
    }
}
