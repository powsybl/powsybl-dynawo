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
import com.powsybl.dynawaltz.models.events.AbstractEventModel
import com.powsybl.dynawaltz.models.events.EventQuadripoleDisconnection
import com.powsybl.dynawaltz.models.events.EventSetPointBoolean
import com.powsybl.iidm.network.Branch
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Line
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link EventModelGroovyExtension} that adds the <pre>EventQuadripoleDisconnection</pre> keyword to the DSL
 *
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(EventModelGroovyExtension.class)
class EventDisconnectionGroovyExtension extends AbstractPureDynamicGroovyExtension<EventModel> implements EventModelGroovyExtension {

    private static final EnumSet<IdentifiableType> connectableEquipments = EnumSet.of(IdentifiableType.GENERATOR, IdentifiableType.LOAD)

    private static final EnumSet<IdentifiableType> connectableQuadripoleEquipments = EnumSet.of(IdentifiableType.LINE, IdentifiableType.TWO_WINDINGS_TRANSFORMER)

    EventDisconnectionGroovyExtension() {
        modelTags = ["Disconnect"]
    }

    @Override
    protected EventQuadripoleDisconnectionBuilder createBuilder(Network network) {
        new EventQuadripoleDisconnectionBuilder(network)
    }

    static class EventQuadripoleDisconnectionBuilder extends AbstractEventModelBuilder {

        boolean disconnectSide = false
        boolean isEquipment = false
        boolean isQuadripoleEquipment = false

        boolean disconnectOrigin = true
        boolean disconnectExtremity = true
        Identifiable<? extends Identifiable> identifiable

        EventQuadripoleDisconnectionBuilder(Network network) {
            super(network)
        }

        void disconnectOnly(Branch.Side side) {
            disconnectSide = true
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
            identifiable = network.getIdentifiable(staticId)
            if (identifiable == null) {
                throw new DslException("Identifiable static id unknown: " + getStaticId())
            }
            isEquipment = connectableEquipments.contains(identifiable.getType())
            isQuadripoleEquipment = connectableQuadripoleEquipments.contains(identifiable.getType())
            if (!isEquipment && !isQuadripoleEquipment) {
                throw new DslException("Equipment " + getStaticId() + " cannot be disconnected")
            } else if(isEquipment && disconnectSide) {
                throw new DslException("Equipment " + getStaticId() + " is not a quadripole")
            }
        }

        @Override
        AbstractEventModel build() {
            checkData()
            if(isEquipment)
                new EventSetPointBoolean(identifiable, startTime)
            else if (isQuadripoleEquipment)
                new EventQuadripoleDisconnection(identifiable, startTime, disconnectOrigin, disconnectExtremity)
        }
    }
}
