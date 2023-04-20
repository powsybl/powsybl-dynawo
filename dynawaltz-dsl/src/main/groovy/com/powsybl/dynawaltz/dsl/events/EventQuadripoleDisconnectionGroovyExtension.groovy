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
import com.powsybl.dynawaltz.models.events.EventQuadripoleDisconnection

/**
 * An implementation of {@link EventModelGroovyExtension} that adds the <pre>EventQuadripoleDisconnection</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(EventModelGroovyExtension.class)
class EventQuadripoleDisconnectionGroovyExtension extends AbstractPureDynamicGroovyExtension<EventModel> implements EventModelGroovyExtension {

    EventQuadripoleDisconnectionGroovyExtension() {
        modelTags = ["EventQuadripoleDisconnection"]
    }

    @Override
    protected EventQuadripoleDisconnectionBuilder createBuilder() {
        new EventQuadripoleDisconnectionBuilder()
    }

    static class EventQuadripoleDisconnectionBuilder extends AbstractEventModelBuilder {

        boolean disconnectOrigin
        boolean disconnectExtremity

        void disconnectOrigin(boolean disconnectOrigin) {
            this.disconnectOrigin = disconnectOrigin
        }

        void disconnectExtremity(boolean disconnectExtremity) {
            this.disconnectExtremity = disconnectExtremity
        }

        @Override
        EventQuadripoleDisconnection build() {
            checkData()
            new EventQuadripoleDisconnection(eventModelId, staticId, startTime, disconnectOrigin, disconnectExtremity)
        }
    }
}
