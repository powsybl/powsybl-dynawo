/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.automatons

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicGroovyExtension
import com.powsybl.dynawaltz.dsl.builders.AbstractPureDynamicModelBuilder
import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomaton
import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Load
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.TwoWindingsTransformer

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(DynamicModelGroovyExtension.class)
class TapChangerBlockingAutomatonGroovyExtension extends AbstractPureDynamicGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    private static final String LIB = "TapChangerBlockingAutomaton"

    TapChangerBlockingAutomatonGroovyExtension() {
        modelTags = [LIB]
    }

    @Override
    protected TCBAutomatonBuilder createBuilder(Network network) {
        new TCBAutomatonBuilder(network, LIB)
    }

    static class TCBAutomatonBuilder extends AbstractPureDynamicModelBuilder {

        List<Load> loads = []
        List<TwoWindingsTransformer> transformers = []
        List<Bus> uMeasurements = []
        List<String> tapChangerAutomatonIds = []

        TCBAutomatonBuilder(Network network, String lib) {
            super(network, lib)
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
            network.getIdentifiable(staticId)?.tap {
                if (!TapChangerBlockingAutomaton.isCompatibleEquipment(type)) {
                    LOGGER.warn("${getLib()}: $type $staticId is not compatible")
                }
            }
        }

        void uMeasurements(String[] staticIds) {
            uMeasurements = staticIds.collect {
                def bus = network.busBreakerView.getBus(it)
                if (!bus) {
                    LOGGER.warn("${getLib()}: $IdentifiableType.BUS static id unknown : $it")
                }
                bus
            }
        }

        @Override
        protected void checkData() {
            if (!uMeasurements) {
                LOGGER.warn("${getLib()}: 'uMeasurements' field is not set")
                isInstantiable = false
            } else {
                uMeasurements -= null
                if (!uMeasurements) {
                    LOGGER.warn("${getLib()}: 'uMeasurements' is empty")
                    isInstantiable = false
                }
            }
            if(!loads && !transformers && !tapChangerAutomatonIds) {
                LOGGER.warn("${getLib()}: 'transformers' field is empty")
                isInstantiable = false
            }
        }

        @Override
        TapChangerBlockingAutomaton build() {
            isInstantiable() ? new TapChangerBlockingAutomaton(dynamicModelId, parameterSetId,
                    transformers, loads, tapChangerAutomatonIds, uMeasurements)
                    : null
        }
    }
}
