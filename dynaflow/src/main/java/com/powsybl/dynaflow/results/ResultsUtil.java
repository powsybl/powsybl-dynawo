/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow.results;

import com.powsybl.security.PostContingencyComputationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static com.powsybl.dynaflow.results.Status.CRITERIA_NON_RESPECTED;
import static com.powsybl.security.PostContingencyComputationStatus.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ResultsUtil {

    private ResultsUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultsUtil.class);

    public static PostContingencyComputationStatus convertStatus(Status status) {
        return switch (status) {
            case CONVERGENCE -> CONVERGED;
            case DIVERGENCE -> SOLVER_FAILED;
            case EXECUTION_PROBLEM, CRITERIA_NON_RESPECTED -> FAILED;
        };
    }

    static Optional<ScenarioResult> createScenarioResult(String id, String status, List<FailedCriterion> failedCriteria) {
        if (id == null || status == null || failedCriteria == null) {
            LOGGER.warn("Inconsistent scenario result entry (id: '{}', status: '{}', failedCriteria: '{}')", id, status, failedCriteria);
        } else {
            try {
                Status statusE = Status.valueOf(status);
                boolean isCriterionError = CRITERIA_NON_RESPECTED == statusE;
                if (isCriterionError && failedCriteria.isEmpty()) {
                    LOGGER.warn("ScenarioResult with {} status should have failed criteria", status);
                    return Optional.empty();
                } else if (!isCriterionError && !failedCriteria.isEmpty()) {
                    LOGGER.warn("ScenarioResult with {} status should not have failed criteria", status);
                    return Optional.empty();
                }
                return Optional.of(new ScenarioResult(id, statusE, failedCriteria));
            } catch (IllegalArgumentException e) {
                logInconsistentEntry("status", status);
            }
        }
        return Optional.empty();
    }

    static Optional<FailedCriterion> createFailedCriterion(String message, String time) {
        if (message == null || time == null) {
            LOGGER.warn("Inconsistent failed criterion entry (message: '{}', time: '{}')", message, time);
        } else {
            try {
                double timeD = Double.parseDouble(time);
                return Optional.of(new FailedCriterion(message, timeD));
            } catch (NumberFormatException e) {
                logInconsistentEntry("time", time);
            }
        }
        return Optional.empty();
    }

    private static void logInconsistentEntry(String fieldName, String message) {
        LOGGER.warn("Inconsistent {} entry '{}'", fieldName, message);
    }
}