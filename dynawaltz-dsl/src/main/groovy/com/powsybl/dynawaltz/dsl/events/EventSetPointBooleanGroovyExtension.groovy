/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.events

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.EventModel
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension
import com.powsybl.dynawaltz.dsl.ModelBuilder
import com.powsybl.dynawaltz.dsl.PowsyblDynawoGroovyExtension
import com.powsybl.dynawaltz.models.events.EventSetPointBoolean

/**
 * An implementation of {@link EventModelGroovyExtension} that adds the <pre>EventSetPointBoolean</pre> keyword to the DSL
 *
 * @author Mathieu BAGUE {@literal <mathieu.bague at rte-france.com>}
 */
@AutoService(EventModelGroovyExtension.class)
class EventSetPointBooleanGroovyExtension extends PowsyblDynawoGroovyExtension<EventModel> implements EventModelGroovyExtension {

    EventSetPointBooleanGroovyExtension() {
        tags = ["EventSetPointBoolean"]
    }

    @Override
    protected EventSetPointBooleanBuilder createBuilder(String currentTag) {
        new EventSetPointBooleanBuilder()
    }

    static class EventSetPointBooleanBuilder implements ModelBuilder<EventModel> {
        String eventModelId
        String staticId
        double startTime
        boolean stateEvent

        void eventModelId(String eventModelId) {
            this.eventModelId = eventModelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void startTime(double startTime) {
            this.startTime = startTime
        }

        void stateEvent(boolean stateEvent) {
            this.stateEvent = stateEvent
        }

        @Override
        EventModel build() {
            if (!staticId) {
                throw new DslException("'staticId' field is not set")
            }
            if (!startTime) {
                throw new DslException("'startTime' field is not set")
            }
            if (!eventModelId) {
                eventModelId = staticId
            }
            new EventSetPointBoolean(eventModelId, staticId, startTime, stateEvent)
        }
    }
}
