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
import com.powsybl.dynawaltz.dsl.builders.BuildersUtil
import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomaton
import com.powsybl.iidm.network.*

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
    protected TCBAutomatonBuilder createBuilder(Network network, Reporter reporter) {
        new TCBAutomatonBuilder(network, LIB, reporter)
    }

    static class TCBAutomatonBuilder extends AbstractPureDynamicModelBuilder {

        List<Load> loads = []
        List<TwoWindingsTransformer> transformers = []
        List<Identifiable> uMeasurements = []
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
            for (staticId in staticIds) {
                def measurementPoint = network.getIdentifiable(staticId)
                if (!measurementPoint || !BuildersUtil.isActionConnectionPoint(measurementPoint.type)) {
                    Reporters.reportStaticIdUnknown(reporter, "uMeasurements", staticId, "BUS/BUSBAR_SECTION")
                } else {
                    uMeasurements << measurementPoint
                }
            }
        }

        void uMeasurements(List<String>[] staticIdsArray) {
            for (staticIds in staticIdsArray) {
                for (staticId in staticIds) {
                    def measurementPoint = network.getIdentifiable(staticId)
                    if (!measurementPoint || !BuildersUtil.isActionConnectionPoint(measurementPoint.type)) {
                        Reporters.reportStaticIdUnknown(reporter, "uMeasurements", staticId, "BUS/BUSBAR_SECTION")
                    } else {
                        uMeasurements << measurementPoint
                        break
                    }
                }
            }
        }

        @Override
        protected void checkData() {
            if (!uMeasurements) {
                Reporters.reportFieldNotSet(reporter, "uMeasurements")
                isInstantiable = false
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
