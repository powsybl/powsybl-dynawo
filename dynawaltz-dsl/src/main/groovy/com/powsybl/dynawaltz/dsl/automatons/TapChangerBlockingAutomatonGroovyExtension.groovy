/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.automatons

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicGroovyExtension
import com.powsybl.dynawaltz.dsl.Reporters
import com.powsybl.dynawaltz.dsl.builders.AbstractPureDynamicModelBuilder
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

    private static final String LIB = "TapChangerBlockingAutomaton"

    TapChangerBlockingAutomatonGroovyExtension() {
        modelTags = [LIB]
    }

    @Override
    protected TCBAutomatonBuilder createBuilder(Network network, Reporter reporter) {
        new TCBAutomatonBuilder(network, LIB, reporter)
    }

    static class TCBAutomatonBuilder extends AbstractPureDynamicModelBuilder {

        List<Load> loads = []
        List<TwoWindingsTransformer> transformers = []
        List<Bus> uMeasurements = []
        List<String> tapChangerAutomatonIds = []

        TCBAutomatonBuilder(Network network, String lib, Reporter reporter) {
            super(network, lib, reporter)
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
                    Reporters.reportStaticIdUnknown(reporter, "uMeasurements", staticId, "LOAD/TWO_WINDINGS_TRANSFORMER")
                }
            }
        }

        void uMeasurements(String[] staticIds) {
            uMeasurements = staticIds.collect {
                def bus = network.busBreakerView.getBus(it)
                if (!bus) {
                    Reporters.reportStaticIdUnknown(reporter, "uMeasurements", it, IdentifiableType.BUS.toString())
                }
                bus
            }
        }

        @Override
        protected void checkData() {
            if (!uMeasurements) {
                Reporters.reportFieldNotSet(reporter, "uMeasurements")
                isInstantiable = false
            } else {
                uMeasurements -= null
                if (!uMeasurements) {
                    Reporters.reportEmptyList(reporter, "uMeasurements")
                    isInstantiable = false
                }
            }
            if(!loads && !transformers && !tapChangerAutomatonIds) {
                Reporters.reportEmptyList(reporter, "transformers")
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
