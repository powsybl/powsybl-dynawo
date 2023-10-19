/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.automatons

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicGroovyExtension
import com.powsybl.dynawaltz.dsl.DslEquipment
import com.powsybl.dynawaltz.dsl.Reporters
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
    protected CurrentLimitAutomatonTwoLevelBuilder createBuilder(Network network, Reporter reporter) {
        new CurrentLimitAutomatonTwoLevelBuilder(network, getLib(), reporter)
    }

    static class CurrentLimitAutomatonTwoLevelBuilder extends CurrentLimitAutomatonGroovyExtension.CurrentLimitAutomatonBuilder {

        protected final DslEquipment<Branch> iMeasurement2
        protected Side iMeasurement2Side

        CurrentLimitAutomatonTwoLevelBuilder(Network network, String lib, Reporter reporter) {
            super(network, lib, reporter)
            iMeasurement2 = new DslEquipment<>("Quadripole", "iMeasurement2")
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
            isInstantiable &= iMeasurement2.checkEquipmentData(reporter)
            if (!iMeasurement2Side) {
                Reporters.reportFieldNotSet(reporter, "iMeasurement2Side")
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
