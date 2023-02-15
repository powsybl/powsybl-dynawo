/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.events

import java.util.function.Consumer

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.EventModel
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension

import com.powsybl.dynawaltz.DynaWaltzProvider
import com.powsybl.dynawaltz.models.events.EventQuadripoleDisconnection

/**
 * An implementation of {@link EventModelGroovyExtension} that adds the <pre>EventQuadripoleDisconnection</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(EventModelGroovyExtension.class)
class EventQuadripoleDisconnectionGroovyExtension implements EventModelGroovyExtension {

    static class EventQuadripoleDisconnectionSpec {
        String eventModelId
        String staticId
        double startTime
        boolean disconnectOrigin
        boolean disconnectExtremity

        void eventModelId(String eventModelId) {
            this.eventModelId = eventModelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void startTime(double startTime) {
            this.startTime = startTime
        }

        void disconnectOrigin(boolean disconnectOrigin) {
            this.disconnectOrigin = disconnectOrigin
        }

        void disconnectExtremity(boolean disconnectExtremity) {
            this.disconnectExtremity = disconnectExtremity
        }
    }

    String getName() {
        return DynaWaltzProvider.NAME
    }
    
    void load(Binding binding, Consumer<EventModel> consumer) {
        binding.EventQuadripoleDisconnection = { Closure<Void> closure ->
            def cloned = closure.clone()
            EventQuadripoleDisconnectionSpec eventQuadripoleDisconnectionSpec = new EventQuadripoleDisconnectionSpec()
    
            cloned.delegate = eventQuadripoleDisconnectionSpec
            cloned()

            if (!eventQuadripoleDisconnectionSpec.staticId) {
                throw new DslException("'staticId' field is not set");
            }
            if (!eventQuadripoleDisconnectionSpec.startTime) {
                throw new DslException("'startTime' field is not set")
            }

            String eventModelId = eventQuadripoleDisconnectionSpec.eventModelId ? eventQuadripoleDisconnectionSpec.eventModelId : eventQuadripoleDisconnectionSpec.staticId
            consumer.accept(new EventQuadripoleDisconnection(eventModelId, eventQuadripoleDisconnectionSpec.staticId,
                    eventQuadripoleDisconnectionSpec.startTime, eventQuadripoleDisconnectionSpec.disconnectOrigin,
                    eventQuadripoleDisconnectionSpec.disconnectExtremity))
        }
    }

}
