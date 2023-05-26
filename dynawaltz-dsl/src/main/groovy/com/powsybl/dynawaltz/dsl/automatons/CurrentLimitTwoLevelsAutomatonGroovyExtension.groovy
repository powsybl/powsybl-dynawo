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
        return "CurrentLimitAutomatonTwoLevels"
    }

    @Override
    protected CurrentLimitAutomatonTwoLevelBuilder createBuilder(Network network) {
        new CurrentLimitAutomatonTwoLevelBuilder(network, getLib())
    }

    static class CurrentLimitAutomatonTwoLevelBuilder extends CurrentLimitAutomatonGroovyExtension.CurrentLimitAutomatonBuilder {

        Branch<? extends Branch> equipment2
        Side side2

        CurrentLimitAutomatonTwoLevelBuilder(Network network, String lib) {
            super(network, lib)
        }

        void iMeasurement1(String staticId) {
            iMeasurement(staticId)
        }

        void iMeasurement1Side(Branch.Side side) {
            iMeasurementSide(side)
        }

        void iMeasurement2(String staticId) {
            equipment2 = network.getBranch(staticId)
            if (!equipment2) {
                throw new DslException("Equipment ${staticId} is not a quadripole")
            }
        }

        void iMeasurement2Side(Branch.Side side) {
            this.side2 = SideConverter.convert(side)
        }

        @Override
        void checkData() {
            super.checkData()
            if (!equipment2) {
                throw new DslException("'iMeasurement2' field is not set")
            }
            if (!side2) {
                throw new DslException("'iMeasurement2Side' field is not set")
            }
        }

        @Override
        CurrentLimitTwoLevelsAutomaton build() {
            checkData()
            new CurrentLimitTwoLevelsAutomaton(dynamicModelId, parameterSetId, equipment, side, equipment2, side2, controlledEquipment, lib)
        }
    }
}
