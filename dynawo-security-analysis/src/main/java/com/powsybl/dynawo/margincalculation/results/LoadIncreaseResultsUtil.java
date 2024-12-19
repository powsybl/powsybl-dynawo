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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class LoadIncreaseResultsUtil {

    private LoadIncreaseResultsUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadIncreaseResultsUtil.class);

    static Optional<LoadIncreaseResult> createLoadIncreaseResult(String loadLevel, String status, List<ScenarioResult> scenarioResults,
                                                                 List<FailedCriterion> failedCriteria) {
        if (loadLevel == null || status == null || failedCriteria == null || scenarioResults == null) {
            LOGGER.warn("Inconsistent load increase result entry (loadLevel: '{}', status: '{}', scenarioResults: '{}', failedCriteria: '{}')",
                    loadLevel, status, scenarioResults, failedCriteria);
        } else {
            try {
                double loadLevelD = Double.parseDouble(loadLevel);
                Status statusE = Status.valueOf(status);
                switch (statusE) {
                    case CONVERGENCE -> {
                        if (scenarioResults.isEmpty()) {
                            LOGGER.warn("LoadIncreaseResult with {} status should have scenario results", status);
                            return Optional.empty();
                        }
                        if (!failedCriteria.isEmpty()) {
                            LOGGER.warn("LoadIncreaseResult with {} status should not have failed criteria", status);
                            return Optional.empty();
                        }
                    }
                    case CRITERIA_NON_RESPECTED -> {
                        if (!scenarioResults.isEmpty()) {
                            LOGGER.warn("LoadIncreaseResult with {} status should not have scenario results", status);
                            return Optional.empty();
                        }
                        if (failedCriteria.isEmpty()) {
                            LOGGER.warn("LoadIncreaseResult with {} status should have failed criteria", status);
                            return Optional.empty();
                        }
                    }
                    case DIVERGENCE, EXECUTION_PROBLEM -> {
                        if (!scenarioResults.isEmpty()) {
                            LOGGER.warn("LoadIncreaseResult with {} status should not have scenario results", status);
                            return Optional.empty();
                        }
                        if (!failedCriteria.isEmpty()) {
                            LOGGER.warn("LoadIncreaseResult with {} status should not have failed criteria", status);
                            return Optional.empty();
                        }
                    }
                }
                return Optional.of(new LoadIncreaseResult(loadLevelD, statusE, scenarioResults, failedCriteria));
            } catch (NumberFormatException e) {
                logInconsistentEntry("loadLevel", loadLevel);
            } catch (IllegalArgumentException e) {
                logInconsistentEntry("status", status);
            }
        }
        return Optional.empty();
    }

    private static void logInconsistentEntry(String fieldName, String message) {
        LOGGER.warn("Inconsistent {} entry '{}'", fieldName, message);
    }
}
