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
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicGroovyExtension
import com.powsybl.dynawaltz.models.events.EventQuadripoleDisconnection
import com.powsybl.iidm.network.Branch
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link EventModelGroovyExtension} that adds the <pre>EventQuadripoleDisconnection</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(EventModelGroovyExtension.class)
class EventQuadripoleDisconnectionGroovyExtension extends AbstractPureDynamicGroovyExtension<EventModel> implements EventModelGroovyExtension {

    private static final EnumSet<IdentifiableType> connectableEquipments = EnumSet.of(IdentifiableType.LINE, IdentifiableType.TWO_WINDINGS_TRANSFORMER)

    EventQuadripoleDisconnectionGroovyExtension() {
        modelTags = ["DisconnectQuadripole"]
    }

    @Override
    protected EventQuadripoleDisconnectionBuilder createBuilder(Network network) {
        new EventQuadripoleDisconnectionBuilder(network)
    }

    static class EventQuadripoleDisconnectionBuilder extends AbstractEventModelBuilder {

        boolean disconnectOrigin = true
        boolean disconnectExtremity = true


        EventQuadripoleDisconnectionBuilder(Network network) {
            super(network)
        }

        void disconnectOnly(Branch.Side side) {
            switch(side) {
                case Branch.Side.ONE :
                    disconnectOrigin = true
                    disconnectExtremity = false
                    break
                case Branch.Side.TWO :
                    disconnectOrigin = false
                    disconnectExtremity = true
                    break
            }
        }

        void checkData() {
            super.checkData()
            Identifiable<?> identifiable = network.getIdentifiable(staticId)
            if (identifiable == null) {
                throw new DslException("Identifiable static id unknown: " + getStaticId())
            }
            if (!connectableEquipments.contains(identifiable.getType())) {
                throw new DslException("Equipment " + getStaticId() + " cannot be disconnected")
            }
        }

        @Override
        EventQuadripoleDisconnection build() {
            checkData()
            new EventQuadripoleDisconnection(staticId, startTime, disconnectOrigin, disconnectExtremity)
        }
    }
}
