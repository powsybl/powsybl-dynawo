/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.events

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.EventModel
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicGroovyExtension
import com.powsybl.dynawaltz.models.events.AbstractEventModel
import com.powsybl.dynawaltz.models.events.EventHvdcDisconnection
import com.powsybl.dynawaltz.models.events.EventQuadripoleDisconnection
import com.powsybl.dynawaltz.models.events.EventInjectionDisconnection
import com.powsybl.iidm.network.Branch
import com.powsybl.iidm.network.HvdcLine
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(EventModelGroovyExtension.class)
class EventDisconnectionGroovyExtension extends AbstractPureDynamicGroovyExtension<EventModel> implements EventModelGroovyExtension {

    private static final EnumSet<IdentifiableType> CONNECTABLE_INJECTIONS = EnumSet.of(IdentifiableType.GENERATOR, IdentifiableType.LOAD)

    private static final EnumSet<IdentifiableType> CONNECTABLE_QUADRIPOLES = EnumSet.of(IdentifiableType.LINE, IdentifiableType.TWO_WINDINGS_TRANSFORMER)

    EventDisconnectionGroovyExtension() {
        modelTags = ["Disconnect"]
    }

    @Override
    protected EventQuadripoleDisconnectionBuilder createBuilder(Network network) {
        new EventQuadripoleDisconnectionBuilder(network)
    }

    static class EventQuadripoleDisconnectionBuilder extends AbstractEventModelBuilder {

        boolean disconnectSide = false
        private disconnectionType = DisconnectionType.NONE

        boolean disconnectOrigin = true
        boolean disconnectExtremity = true
        Identifiable<? extends Identifiable> identifiable

        private enum DisconnectionType {INJECTION, QUADRIPOLE, HVDC, NONE}

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
            disconnectionType()
            if (DisconnectionType.INJECTION == disconnectionType && disconnectSide) {
                throw new DslException("'disconnectSide' has been set but ${identifiable.getType() } ${getStaticId()} is not a quadripole with a disconnectable side")
            }
        }

        private void disconnectionType() {
            IdentifiableType type = identifiable.getType()
            if (CONNECTABLE_INJECTIONS.contains(type)) {
                disconnectionType = DisconnectionType.INJECTION
            } else if (CONNECTABLE_QUADRIPOLES.contains(type)) {
                disconnectionType = DisconnectionType.QUADRIPOLE
            } else if (IdentifiableType.HVDC_LINE == type) {
                disconnectionType = DisconnectionType.HVDC
            }
        }

        @Override
        AbstractEventModel build() {
            checkData()
            switch(disconnectionType) {
                case DisconnectionType.INJECTION :
                    return new EventInjectionDisconnection(identifiable, startTime)
                case DisconnectionType.QUADRIPOLE :
                    return new EventQuadripoleDisconnection(identifiable, startTime, disconnectOrigin, disconnectExtremity)
                case DisconnectionType.HVDC :
                    return new EventHvdcDisconnection((HvdcLine) identifiable, startTime, disconnectOrigin, disconnectExtremity)
                default :
                    throw new DslException("Equipment ${getStaticId()} cannot be disconnected")
            }
        }
    }
}
