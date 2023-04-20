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
import com.powsybl.dynawaltz.dsl.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicGroovyExtension
import com.powsybl.dynawaltz.models.automatons.CurrentLimitAutomaton
import com.powsybl.dynawaltz.models.automatons.CurrentLimitTwoLevelsAutomaton
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class CurrentLimitTwoLevelsAutomatonGroovyExtension extends AbstractPureDynamicGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    CurrentLimitTwoLevelsAutomatonGroovyExtension() {
        modelTags = ["CurrentLimitAutomatonTwoLevels"]
    }

    @Override
    protected CurrentLimitAutomatonTwoLevelBuilder createBuilder(String currentTag, Network network) {
        new CurrentLimitAutomatonTwoLevelBuilder(network)
    }

    static class CurrentLimitAutomatonTwoLevelBuilder extends AbstractDynamicModelBuilder {

        Network network
        Identifiable<? extends Identifiable> equipment

        CurrentLimitAutomatonTwoLevelBuilder(Network network) {
            this.network = network
        }

        @Override
        void checkData() {
            super.checkData()
            equipment = network.getIdentifiable(staticId)
            if (equipment == null) {
                throw new DslException("Identifiable static id unknown: ${getStaticId()}")
            }
            if(!CurrentLimitAutomaton.isCompatibleEquipment(equipment.getType())) {
                throw new DslException("Equipment ${staticId} is not a quadripole")
            }
        }

        @Override
        CurrentLimitTwoLevelsAutomaton build() {
            checkData()
            new CurrentLimitTwoLevelsAutomaton(dynamicModelId, parameterSetId, equipment)
        }
    }
}
