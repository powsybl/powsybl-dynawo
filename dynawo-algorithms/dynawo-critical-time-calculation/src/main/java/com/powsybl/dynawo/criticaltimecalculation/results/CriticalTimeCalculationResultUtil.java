/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public final class CriticalTimeCalculationResultUtil {

    private CriticalTimeCalculationResultUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CriticalTimeCalculationResultUtil.class);

    static Optional<CriticalTimeCalculationResult> createCriticalTimeCalculationResult(String id, String status,
                                                                                       String criticalTime) {
        if (id == null || status == null) {
            LOGGER.warn("Inconsistent load increase result entry (id: '{}', status: '{}', criticalTime: '{}' )", id, status, criticalTime);
        } else {
            double criticalTimeValue = (criticalTime != null) ? Double.parseDouble(criticalTime) : Double.NaN;
            try {
                return buildCriticalTimeCalculationResult(id, Status.valueOf(status), criticalTimeValue);
            } catch (IllegalArgumentException e) {
                logInconsistentEntry("status", status);
            }
        }
        return Optional.empty();
    }

    private static Optional<CriticalTimeCalculationResult> buildCriticalTimeCalculationResult(String id, Status status,
                                                                                              double criticalTime) {
        switch (status) {
            case RESULT_FOUND -> {
                if (Double.isNaN(criticalTime)) {
                    LOGGER.warn("ScenarioResults with {} status should have critical time", status);
                    return Optional.empty();
                }
            }
            case CT_BELOW_MIN_BOUND, CT_ABOVE_MAX_BOUND -> {
                if (!Double.isNaN(criticalTime)) {
                    LOGGER.warn("ScenarioResults with {} status should not have critical time", status);
                    return Optional.empty();
                }
            }
        }
        return Optional.of(new CriticalTimeCalculationResult(id, status, criticalTime));
    }

    private static void logInconsistentEntry(String fieldName, String message) {
        LOGGER.warn("Inconsistent {} entry '{}'", fieldName, message);
    }

}
