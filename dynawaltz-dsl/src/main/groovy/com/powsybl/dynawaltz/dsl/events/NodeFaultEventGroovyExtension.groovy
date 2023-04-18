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
import com.powsybl.dynawaltz.models.events.NodeFaultEvent
import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(EventModelGroovyExtension.class)
class NodeFaultEventGroovyExtension extends AbstractPureDynamicGroovyExtension<EventModel> implements EventModelGroovyExtension {

    NodeFaultEventGroovyExtension() {
        modelTags = ["NodeFault"]
    }

    @Override
    protected NodeFaultEventBuilder createBuilder(String tag, Network network) {
        new NodeFaultEventBuilder(network)
    }

    static class NodeFaultEventBuilder extends AbstractEventModelBuilder {

        Bus bus
        double faultTime
        double rPu
        double xPu

        NodeFaultEventBuilder(Network network) {
            super(network)
        }

        void faultTime(double faultTime) {
            this.faultTime = faultTime
        }

        void RPu(double rPu) {
            this.rPu = rPu
        }

        void XPu(double xPu) {
            this.xPu = xPu
        }

        void checkData() {
            super.checkData()
            this.bus = network.getBusBreakerView().getBus(staticId)
            if (bus == null) {
                throw new DslException("Bus static id unknown: " + staticId)
            }
            if (faultTime <= 0) {
                throw new DslException("NodeFault ${bus.getId()} fault time should be strictly positive (${faultTime})")
            }
            if (rPu < 0) {
                throw new DslException("NodeFault ${bus.getId()} RPu should be positive (${rPu})")
            }
            if (xPu < 0) {
                throw new DslException("NodeFault ${bus.getId()} XPu should be positive (${xPu})")
            }
        }

        @Override
        AbstractEventModel build() {
            checkData()
            new NodeFaultEvent(bus, startTime, faultTime, rPu, xPu)
        }
    }
}
