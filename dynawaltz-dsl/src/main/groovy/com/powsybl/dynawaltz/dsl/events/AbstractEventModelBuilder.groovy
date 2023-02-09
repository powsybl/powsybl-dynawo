/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.dsl.events

import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.EventModel
import com.powsybl.dynawaltz.dsl.ModelBuilder

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractEventModelBuilder implements ModelBuilder<EventModel> {

    String eventModelId
    String staticId
    double startTime

    void eventModelId(String eventModelId) {
        this.eventModelId = eventModelId
    }

    void staticId(String staticId) {
        this.staticId = staticId
    }

    void startTime(double startTime) {
        this.startTime = startTime
    }

    void setupBuild() {
        if (!staticId) {
            throw new DslException("'staticId' field is not set")
        }
        if (!startTime) {
            throw new DslException("'startTime' field is not set")
        }
        if (!eventModelId) {
            eventModelId = staticId
        }
    }

    @Override
    abstract EventModel build();
}
