/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.events

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.EventModel
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicGroovyExtension
import com.powsybl.dynawaltz.dsl.DslEquipment
import com.powsybl.dynawaltz.dsl.builders.AbstractEventModelBuilder
import com.powsybl.dynawaltz.models.events.NodeFaultEvent
import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(EventModelGroovyExtension.class)
class NodeFaultEventGroovyExtension extends AbstractPureDynamicGroovyExtension<EventModel> implements EventModelGroovyExtension {

    private static final String TAG = "NodeFault"

    NodeFaultEventGroovyExtension() {
        modelTags = [TAG]
    }

    @Override
    protected NodeFaultEventBuilder createBuilder(Network network) {
        new NodeFaultEventBuilder(network, TAG)
    }

    static class NodeFaultEventBuilder extends AbstractEventModelBuilder {

        protected double faultTime
        protected double rPu
        protected double xPu

        NodeFaultEventBuilder(Network network,String tag) {
            super(network, new DslEquipment<Bus>(IdentifiableType.BUS), tag)
        }

        void faultTime(double faultTime) {
            this.faultTime = faultTime
        }

        void rPu(double rPu) {
            this.rPu = rPu
        }

        void xPu(double xPu) {
            this.xPu = xPu
        }

        protected Bus findEquipment(String staticId) {
            network.getBusBreakerView().getBus(staticId)
        }

        @Override
        void checkData() {
            super.checkData()
            if (faultTime <= 0) {
                LOGGER.warn("Fault time should be strictly positive (${faultTime})")
                isInstantiable = false
            }
            if (rPu < 0) {
                LOGGER.warn("rPu should be positive (${rPu})")
                isInstantiable = false
            }
            if (xPu < 0) {
                LOGGER.warn("xPu should be positive (${xPu})")
                isInstantiable = false
            }
        }

        @Override
        NodeFaultEvent build() {
            isInstantiable() ? new NodeFaultEvent(dslEquipment.equipment, startTime, faultTime, rPu, xPu) : null
        }
    }
}
