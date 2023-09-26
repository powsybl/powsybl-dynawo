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
import com.powsybl.dynawaltz.dsl.DslFilteredEquipment
import com.powsybl.dynawaltz.dsl.builders.AbstractEventModelBuilder
import com.powsybl.dynawaltz.models.events.EventActivePowerVariation
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.Injection
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(EventModelGroovyExtension.class)
class EventActivePowerVariationGroovyExtension extends AbstractPureDynamicGroovyExtension<EventModel> implements EventModelGroovyExtension {

    private static final String TAG = "Step"

    EventActivePowerVariationGroovyExtension() {
        modelTags = [TAG]
    }

    @Override
    protected EventAPVBuilder createBuilder(Network network) {
        new EventAPVBuilder(network, TAG)
    }

    static class EventAPVBuilder extends AbstractEventModelBuilder<Injection> {

        protected double deltaP

        EventAPVBuilder(Network network, String tag) {
            super(network, new DslFilteredEquipment<Injection>("Generator/Load", EventActivePowerVariation::isConnectable), tag)
        }

        void deltaP(double deltaP) {
            this.deltaP = deltaP
        }

        void checkData() {
            super.checkData()
            if (dslEquipment.equipment && !EventActivePowerVariation.isConnectable(dslEquipment.equipment.type)) {
                LOGGER.warn("${getLib()}: ${dslEquipment.equipment?.type} ${dslEquipment.staticId} cannot be disconnected")
                isInstantiable = false
            }
            if (!deltaP) {
                LOGGER.warn("${getLib()}: 'deltaP' field is not set")
                isInstantiable = false
            }
        }

        @Override
        protected Identifiable findEquipment(String staticId) {
            network.getIdentifiable(staticId)
        }

        @Override
        EventActivePowerVariation build() {
            isInstantiable() ? new EventActivePowerVariation(dslEquipment.equipment, startTime, deltaP) : null
        }
    }
}
