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
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link EventModelGroovyExtension} that adds the <pre>EventSetPointBoolean</pre> keyword to the DSL
 *
 * @author Mathieu BAGUE {@literal <mathieu.bague at rte-france.com>}
 */
@AutoService(EventModelGroovyExtension.class)
class EventSetPointBooleanGroovyExtension extends AbstractPureDynamicGroovyExtension<EventModel> implements EventModelGroovyExtension {

    EventSetPointBooleanGroovyExtension() {
        modelTags = ["Disconnect"]
    }

    @Override
    protected EventSetPointBooleanBuilder createBuilder(Network network) {
        new EventSetPointBooleanBuilder(network)
    }

    static class EventSetPointBooleanBuilder extends AbstractEventModelBuilder {

        EventSetPointBooleanBuilder(Network network) {
            super(network)
        }

        @Override
        EventModel build() {
            checkData()
            new EventSetPointBoolean(staticId, startTime)
        }
    }
}
