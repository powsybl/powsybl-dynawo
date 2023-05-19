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
import com.powsybl.dynawaltz.dsl.AbstractEventGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicModelGroovyExtension
import com.powsybl.dynawaltz.models.events.AbstractEventModel
import com.powsybl.dynawaltz.models.events.EventActivePowerVariation
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(EventModelGroovyExtension.class)
class EventActivePowerVariationGroovyExtension extends AbstractEventGroovyExtension<EventActivePowerVariation> {

    private static final EnumSet<IdentifiableType> connectableEquipments = EnumSet.of(IdentifiableType.GENERATOR, IdentifiableType.LOAD)

    EventActivePowerVariationGroovyExtension() {
        modelTags = ["Step"]
    }

    @Override
    protected EventAPVBuilder createBuilder(Network network) {
        new EventAPVBuilder(network)
    }

    static class EventAPVBuilder extends AbstractEventModelBuilder {

        double deltaP
        Identifiable<? extends Identifiable> identifiable

        EventAPVBuilder(Network network) {
            super(network)
        }

        void deltaP(double deltaP) {
            this.deltaP = deltaP
        }

        void checkData() {
            super.checkData()
            identifiable = network.getIdentifiable(staticId)
            if (identifiable == null) {
                throw new DslException("Identifiable static id unknown: " + getStaticId())
            }
            if (!EventActivePowerVariation.isConnectable(identifiable.getType())) {
                throw new DslException("Equipment ${getStaticId()} cannot be disconnected")
            }
        }

        @Override
        EventActivePowerVariation build() {
            checkData()
            new EventActivePowerVariation(identifiable, startTime, deltaP)
        }
    }
}
