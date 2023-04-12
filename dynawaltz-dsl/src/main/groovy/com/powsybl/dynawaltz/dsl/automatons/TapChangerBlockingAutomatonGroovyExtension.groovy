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
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicModelBuilder
import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomaton
import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Load
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.TwoWindingsTransformer

import java.util.stream.Collectors

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
        List<Bus> uMeasurement = []

        TCBAutomatonBuilder(Network network) {
            this.network = network
        }

        void transformers(String[] staticIds) {
            staticIds.each {
                Identifiable<? extends Identifiable> equipment = checkEquipment(it)
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

        Identifiable<? extends Identifiable> checkEquipment(String staticId) {
            Identifiable<? extends Identifiable> equipment = network.getIdentifiable(staticId)
            if (equipment == null) {
                throw new DslException("Equipment static id unknown: " + staticId)
            }
            if (!TapChangerBlockingAutomaton.isCompatibleEquipment(equipment.getType())) {
                throw new DslException(equipment.getType().toString() + " " + staticId + " is not compatible")
            }
            return equipment
        }

        void UMeasurement(String staticId) {
            Bus bus = network.getBusBreakerView().getBus(staticId)
            if (bus == null) {
                throw new DslException("Bus static id unknown: " + staticId)
            }
            uMeasurement.add(bus)
        }

        void UMeasurement(List<String> staticIds) {
            uMeasurement = staticIds.stream()
                    .map {
                        Bus bus = network.getBusBreakerView().getBus(it)
                        if (bus == null) {
                            throw new DslException("Bus static id unknown: " + it)
                        }
                        return bus
                    }
                    .collect(Collectors.toList())
        }

        @Override
        TapChangerBlockingAutomaton build() {
            checkData()
            new TapChangerBlockingAutomaton(dynamicModelId, parameterSetId, transformers, loads, uMeasurement)
        }
    }
}
