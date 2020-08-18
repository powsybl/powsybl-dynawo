/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.events

import java.util.function.Consumer

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.EventModel
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension
import com.powsybl.dynawo.events.EventQuadripoleDisconnection

/**
 * An implementation of {@link EventModelGroovyExtension} that adds the <pre>EventQuadripoleDisconnection</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(EventModelGroovyExtension.class)
class EventQuadripoleDisconnectionGroovyExtension implements EventModelGroovyExtension {

    static class EventQuadripoleDisconnectionSpec {
        String dynamicModelId
        String staticId
        String parameterSetId

        void dynamicModelId(String dynamicModelId) {
            this.dynamicModelId = dynamicModelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void parameterSetId(String parameterSetId) {
            this.parameterSetId = parameterSetId
        }
    }

    String getName() {
        return "Dynawo"
    }
    
    void load(Binding binding, Consumer<EventModel> consumer) {
        binding.EventQuadripoleDisconnection = { Closure<Void> closure ->
            def cloned = closure.clone()
            EventQuadripoleDisconnectionSpec cventQuadripoleDisconnectionSpec = new EventQuadripoleDisconnectionSpec()
    
            cloned.delegate = cventQuadripoleDisconnectionSpec
            cloned()

            if (!cventQuadripoleDisconnectionSpec.staticId) {
                throw new DslException("'staticId' field is not set");
            }
            if (!cventQuadripoleDisconnectionSpec.parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }

            String dynamicModelId = cventQuadripoleDisconnectionSpec.dynamicModelId ? cventQuadripoleDisconnectionSpec.dynamicModelId : cventQuadripoleDisconnectionSpec.staticId
            consumer.accept(new EventQuadripoleDisconnection(dynamicModelId, cventQuadripoleDisconnectionSpec.staticId, cventQuadripoleDisconnectionSpec.parameterSetId))
        }
    }

}
