/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
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
import com.powsybl.dynawaltz.dsl.DslEquipment
import com.powsybl.dynawaltz.models.Side
import com.powsybl.dynawaltz.models.automatons.CurrentLimitTwoLevelsAutomaton
import com.powsybl.dynawaltz.models.utils.SideConverter
import com.powsybl.iidm.network.Branch
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class CurrentLimitTwoLevelsAutomatonGroovyExtension extends AbstractPureDynamicGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    CurrentLimitTwoLevelsAutomatonGroovyExtension() {
        modelTags = [getLib()]
    }

    protected String getLib() {
        "CurrentLimitAutomatonTwoLevels"
    }

    @Override
    protected CurrentLimitAutomatonTwoLevelBuilder createBuilder(Network network) {
        new CurrentLimitAutomatonTwoLevelBuilder(network, getLib())
    }

    static class CurrentLimitAutomatonTwoLevelBuilder extends CurrentLimitAutomatonGroovyExtension.CurrentLimitAutomatonBuilder {

        protected final DslEquipment<Branch> iMeasurement2
        protected Side iMeasurement2Side

        CurrentLimitAutomatonTwoLevelBuilder(Network network, String lib) {
            super(network, lib)
            iMeasurement2 = new DslEquipment<>("I measurement 2 quadripole", "iMeasurement2")
        }

        void iMeasurement1(String staticId) {
            iMeasurement(staticId)
        }

        void iMeasurement1Side(Branch.Side side) {
            iMeasurementSide(side)
        }

        void iMeasurement2(String staticId) {
            iMeasurement2.addEquipment(staticId, network::getBranch)
        }

        void iMeasurement2Side(Branch.Side side) {
            this.iMeasurement2Side = SideConverter.convert(side)
        }

        @Override
        void checkData() {
            super.checkData()
            isInstantiable &= iMeasurement2.checkEquipmentData(LOGGER, getLib())
            if (!iMeasurement2Side) {
                LOGGER.warn("${getLib()}: 'iMeasurement2Side' field is not set")
                isInstantiable = false
            }
        }

        @Override
        CurrentLimitTwoLevelsAutomaton build() {
            isInstantiable() ? new CurrentLimitTwoLevelsAutomaton(dynamicModelId, parameterSetId,
                    iMeasurement.equipment, iMeasurementSide, iMeasurement2.equipment, iMeasurement2Side,
                    controlledEquipment.equipment, lib)
                    : null
        }
    }
}
