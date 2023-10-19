/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.events

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.EventModel
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicGroovyExtension
import com.powsybl.dynawaltz.dsl.DslEquipment
import com.powsybl.dynawaltz.dsl.Reporters
import com.powsybl.dynawaltz.dsl.builders.AbstractEventModelBuilder
import com.powsybl.dynawaltz.models.events.AbstractEvent
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

    private static final String TAG = "Disconnect"

    private static final EnumSet<IdentifiableType> CONNECTABLE_INJECTIONS = EnumSet.of(IdentifiableType.GENERATOR, IdentifiableType.LOAD)

    private static final EnumSet<IdentifiableType> CONNECTABLE_QUADRIPOLES = EnumSet.of(IdentifiableType.LINE, IdentifiableType.TWO_WINDINGS_TRANSFORMER)

    EventDisconnectionGroovyExtension() {
        modelTags = [TAG]
    }

    @Override
    protected EventQuadripoleDisconnectionBuilder createBuilder(Network network, Reporter reporter) {
        new EventQuadripoleDisconnectionBuilder(network, TAG, reporter)
    }

    static class EventQuadripoleDisconnectionBuilder extends AbstractEventModelBuilder<Identifiable> {

        private boolean disconnectSide = false
        private disconnectionType = DisconnectionType.NONE

        protected boolean disconnectOrigin = true
        protected boolean disconnectExtremity = true

        private enum DisconnectionType {INJECTION, QUADRIPOLE, HVDC, NONE}

        EventQuadripoleDisconnectionBuilder(Network network, String tag, Reporter reporter) {
            super(network, new DslEquipment<Identifiable>("Disconnectable equipment"), tag, reporter)
        }

        void disconnectOnly(Branch.Side side) {
            disconnectSide = true
            switch (side) {
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
            setDisconnectionType(dslEquipment?.equipment?.type)
            if(dslEquipment.equipment) {
                if (disconnectionType == DisconnectionType.NONE) {
                    Reporters.reportStaticIdUnknown(reporter, "staticId", dslEquipment.staticId, "Disconnectable equipment")
                    isInstantiable = false
                }
                if (DisconnectionType.INJECTION == disconnectionType && disconnectSide) {
                    Reporters.reportFieldSetWithWrongEquipment(reporter, "disconnectSide", dslEquipment.equipment?.type, dslEquipment.staticId)
                    isInstantiable = false
                }
            }
        }

        void setDisconnectionType(IdentifiableType type) {
            if (type) {
                if (CONNECTABLE_INJECTIONS.contains(type)) {
                    disconnectionType = DisconnectionType.INJECTION
                } else if (CONNECTABLE_QUADRIPOLES.contains(type)) {
                    disconnectionType = DisconnectionType.QUADRIPOLE
                } else if (IdentifiableType.HVDC_LINE == type) {
                    disconnectionType = DisconnectionType.HVDC
                }
            }
        }

        @Override
        protected Identifiable findEquipment(String staticId) {
            network.getIdentifiable(staticId)
        }

        @Override
        AbstractEvent build() {
            if (isInstantiable()) {
                switch(disconnectionType) {
                    case DisconnectionType.INJECTION :
                        return new EventInjectionDisconnection(dslEquipment.equipment, startTime)
                    case DisconnectionType.QUADRIPOLE :
                        return new EventQuadripoleDisconnection(dslEquipment.equipment, startTime, disconnectOrigin, disconnectExtremity)
                    case DisconnectionType.HVDC :
                        return new EventHvdcDisconnection((HvdcLine) dslEquipment.equipment, startTime, disconnectOrigin, disconnectExtremity)
                    default :
                        return null
                }
            } else {
                null
            }
        }
    }
}
