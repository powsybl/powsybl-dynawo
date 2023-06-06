/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.timeline;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class Event {

    private final double time;

    private final String modelName;
    private final String message;

    public Event(double time, String modelName, String message) {
        this.time = time;
        this.modelName = modelName;
        this.message = message;
    }

    public double getTime() {
        return time;
    }

    public String getModelName() {
        return modelName;
    }

    public String getMessage() {
        return message;
    }
}
