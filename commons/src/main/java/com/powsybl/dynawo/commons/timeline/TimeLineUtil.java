/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.timeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public final class TimeLineUtil {

    private TimeLineUtil() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeLineUtil.class);

    static Optional<Event> createEvent(String time, String modelName, String message) {
        if (time == null || modelName == null || message == null) {
            LOGGER.warn("Inconsistent event entry (time: '{}', modelName: '{}', message: '{}')", time, modelName, message);
        } else {
            try {
                double timeD = Double.parseDouble(time);
                return Optional.of(new Event(timeD, modelName, message));
            } catch (NumberFormatException e) {
                LOGGER.warn("Inconsistent time entry '{}'", time);
            }
        }
        return Optional.empty();
    }
}
