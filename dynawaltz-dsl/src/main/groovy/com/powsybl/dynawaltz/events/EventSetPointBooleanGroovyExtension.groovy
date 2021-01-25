/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.events

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.EventModel
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension

import com.powsybl.dynawaltz.DynaWaltzProvider
import com.powsybl.dynawaltz.events.EventSetPointBoolean

import java.util.function.Consumer

/**
 * An implementation of {@link EventModelGroovyExtension} that adds the <pre>EventSetPointBoolean</pre> keyword to the DSL
 *
 * @author Mathieu BAGUE {@literal <mathieu.bague at rte-france.com>}
 */
@AutoService(EventModelGroovyExtension.class)
class EventSetPointBooleanGroovyExtension implements EventModelGroovyExtension {

    static class EventSetPointBooleanSpec {
        String eventModelId
        String staticId
        String parameterSetId

        void eventModelId(String eventModelId) {
            this.eventModelId = eventModelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void parameterSetId(String parameterSetId) {
            this.parameterSetId = parameterSetId
        }
    }

    String getName() {
        return DynaWaltzProvider.NAME
    }

    void load(Binding binding, Consumer<EventModel> consumer) {
        binding.EventSetPointBoolean = { Closure<Void> closure ->
            def cloned = closure.clone()
            EventSetPointBooleanSpec eventSetPointBooleanSpec = new EventSetPointBooleanSpec()

            cloned.delegate = eventSetPointBooleanSpec
            cloned()

            if (!eventSetPointBooleanSpec.staticId) {
                throw new DslException("'staticId' field is not set");
            }
            if (!eventSetPointBooleanSpec.parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }

            String eventModelId = eventSetPointBooleanSpec.eventModelId ? eventSetPointBooleanSpec.eventModelId : eventSetPointBooleanSpec.staticId
            consumer.accept(new EventSetPointBoolean(eventModelId, eventSetPointBooleanSpec.staticId, eventSetPointBooleanSpec.parameterSetId))
        }
    }
}
