/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.contingency.results;

import java.util.Collections;
import java.util.List;

/**
 * Contingency scenario result
 * @param id Contingency id
 * @param status Result status
 * @param failedCriteria List of failed criterion
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record ScenarioResult(String id, Status status, List<FailedCriterion> failedCriteria) {

    public ScenarioResult(String id, Status status) {
        this(id, status, Collections.emptyList());
    }
}
