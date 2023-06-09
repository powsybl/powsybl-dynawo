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

        Branch<? extends Branch> iMeasurement2
        Side iMeasurement2Side

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
            iMeasurement2 = network.getBranch(staticId)
            if (!iMeasurement2) {
                throw new DslException("Equipment ${staticId} is not a quadripole")
            }
        }

        void iMeasurement2Side(Branch.Side side) {
            this.iMeasurement2Side = SideConverter.convert(side)
        }

        @Override
        void checkData() {
            super.checkData()
            if (!iMeasurement2) {
                throw new DslException("'iMeasurement2' field is not set")
            }
            if (!iMeasurement2Side) {
                throw new DslException("'iMeasurement2Side' field is not set")
            }
        }

        @Override
        CurrentLimitTwoLevelsAutomaton build() {
            checkData()
            new CurrentLimitTwoLevelsAutomaton(dynamicModelId, parameterSetId, iMeasurement, iMeasurementSide, iMeasurement2, iMeasurement2Side, controlledEquipment, lib)
        }
    }
}
