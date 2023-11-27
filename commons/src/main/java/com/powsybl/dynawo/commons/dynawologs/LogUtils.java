/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.dynawologs;

import com.powsybl.commons.reporter.TypedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class LogUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);
    private static final String EMPTY_START = "======";
    private static final String EMPTY_BREAKER = "------";

    private LogUtils() {
    }

    public static Optional<LogEntry> createLog(String severity, String message) {
        if (severity == null) {
            LOGGER.warn("Inconsistent log entry (modelName: '{}', message: '{}')", severity, message);
        } else {
            if (emptyMessage(message)) {
                LOGGER.debug("Empty message, the entry will be skipped : {}", message);
            } else {
                try {
                    return Optional.of(new LogEntry(convertDynawoLog(severity), message));
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Inconsistent severity entry '{}'", severity);
                }
            }
        }
        return Optional.empty();
    }

    private static TypedValue convertDynawoLog(String severity) {
        return switch (severity) {
            case "DEBUG" -> TypedValue.DEBUG_SEVERITY;
            case "INFO" -> TypedValue.INFO_SEVERITY;
            case "WARN" -> TypedValue.WARN_SEVERITY;
            case "ERROR" -> TypedValue.ERROR_SEVERITY;
            default -> throw new IllegalArgumentException();
        };
    }

    private static boolean emptyMessage(String message) {
        return message == null || message.startsWith(EMPTY_BREAKER) || message.startsWith(EMPTY_START);
    }
}
