/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.events

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.EventModel
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicGroovyExtension
import com.powsybl.dynawaltz.models.events.EventSetPointBoolean

/**
 * An implementation of {@link EventModelGroovyExtension} that adds the <pre>EventSetPointBoolean</pre> keyword to the DSL
 *
 * @author Mathieu BAGUE {@literal <mathieu.bague at rte-france.com>}
 */
@AutoService(EventModelGroovyExtension.class)
class EventSetPointBooleanGroovyExtension extends AbstractPureDynamicGroovyExtension<EventModel> implements EventModelGroovyExtension {

    EventSetPointBooleanGroovyExtension() {
        modelTags = ["EventSetPointBoolean"]
    }

    @Override
    protected EventSetPointBooleanBuilder createBuilder() {
        new EventSetPointBooleanBuilder()
    }

    static class EventSetPointBooleanBuilder extends AbstractEventModelBuilder {

        boolean stateEvent

        void stateEvent(boolean stateEvent) {
            this.stateEvent = stateEvent
        }

        @Override
        EventModel build() {
            checkData()
            new EventSetPointBoolean(eventModelId, staticId, startTime, stateEvent)
        }
    }
}
