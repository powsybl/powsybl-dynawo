/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.automatons

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicGroovyExtension
import com.powsybl.dynawaltz.dsl.models.builders.AbstractPureDynamicModelBuilder
import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomaton
import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Load
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.TwoWindingsTransformer

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class TapChangerBlockingAutomatonGroovyExtension extends AbstractPureDynamicGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    TapChangerBlockingAutomatonGroovyExtension() {
        modelTags = ["TapChangerBlockingAutomaton"]
    }

    @Override
    protected TCBAutomatonBuilder createBuilder(Network network) {
        new TCBAutomatonBuilder(network)
    }

    static class TCBAutomatonBuilder extends AbstractPureDynamicModelBuilder {

        Network network
        List<Load> loads = []
        List<TwoWindingsTransformer> transformers = []
        List<Bus> uMeasurements = []
        List<String> tapChangerAutomatonIds = []

        TCBAutomatonBuilder(Network network) {
            this.network = network
        }

        void transformers(String[] staticIds) {
            staticIds.each {
                Identifiable<? extends Identifiable> equipment = checkEquipment(it)
                if (equipment == null) {
                    tapChangerAutomatonIds.add(it)
                } else {
                    switch (equipment.getType()) {
                        case IdentifiableType.LOAD:
                            loads.add((Load)equipment)
                            break
                        case IdentifiableType.TWO_WINDINGS_TRANSFORMER:
                            transformers.add((TwoWindingsTransformer)equipment)
                            break
                    }
                }
            }
        }

        Identifiable<? extends Identifiable> checkEquipment(String staticId) {
            Identifiable<? extends Identifiable> equipment = network.getIdentifiable(staticId)
            if (equipment != null && !TapChangerBlockingAutomaton.isCompatibleEquipment(equipment.getType())) {
                throw new DslException(equipment.getType().toString() + " " + staticId + " is not compatible")
            }
            return equipment
        }

        void uMeasurements(String[] staticIds) {
            uMeasurements = staticIds.collect {
                Bus bus = network.getBusBreakerView().getBus(it)
                if (bus == null) {
                    throw new DslException("Bus static id unknown: " + it)
                }
                return bus
            }
        }

        @Override
        TapChangerBlockingAutomaton build() {
            checkData()
            new TapChangerBlockingAutomaton(dynamicModelId, parameterSetId, transformers, loads, tapChangerAutomatonIds, uMeasurements)
        }
    }
}
