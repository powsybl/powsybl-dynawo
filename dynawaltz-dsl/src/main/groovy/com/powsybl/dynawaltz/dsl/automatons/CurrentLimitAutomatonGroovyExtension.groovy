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
import com.powsybl.dynawaltz.models.Side
import com.powsybl.dynawaltz.models.automatons.CurrentLimitAutomaton
import com.powsybl.dynawaltz.models.utils.SideConverter
import com.powsybl.iidm.network.Branch
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>CurrentLimitAutomaton</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class CurrentLimitAutomatonGroovyExtension extends AbstractPureDynamicGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    CurrentLimitAutomatonGroovyExtension() {
        modelTags = [getLib()]
    }

    @Override
    protected CurrentLimitAutomatonBuilder createBuilder(Network network) {
        new CurrentLimitAutomatonBuilder(network, getLib())
    }

    protected String getLib() {
        return "CurrentLimitAutomaton"
    }

    static class CurrentLimitAutomatonBuilder extends AbstractPureDynamicModelBuilder {

        Network network
        Branch<? extends Branch> iMeasurement
        Side iMeasurementSide
        Branch<? extends Branch> controlledEquipment
        String lib

        CurrentLimitAutomatonBuilder(Network network, String lib) {
            this.network = network
            this.lib = lib
        }

        void iMeasurement(String staticId) {
            iMeasurement = network.getBranch(staticId)
            /*if (iMeasurement == null) {
                throw new DslException("I measurement equipment ${staticId} is not a quadripole")
            }*/
        }

        void iMeasurementSide(Branch.Side side) {
            this.iMeasurementSide = SideConverter.convert(side)
        }

        void controlledQuadripole(String staticId) {
            controlledEquipment = network.getBranch(staticId)
            /*if (controlledEquipment == null) {
                throw new DslException("Controlled equipment ${staticId} is not a quadripole")
            }*/
        }

        @Override
        void checkData() {
            super.checkData()
            /*if (!iMeasurement) {
                throw new DslException("'iMeasurement' field is not set")
            }*/
            if (!iMeasurementSide) {
                throw new DslException("'iMeasurementSide' field is not set")
            }
            /*if (!controlledEquipment) {
                throw new DslException("'controlledEquipment' field is not set")
            }*/
        }

        @Override
        CurrentLimitAutomaton build() {
            checkData()
            if (iMeasurement && controlledEquipment) {
                new CurrentLimitAutomaton(dynamicModelId, parameterSetId, iMeasurement, iMeasurementSide, controlledEquipment, lib)
            } else {
                println("CurrentLimitAutomaton " + dynamicModelId + " not instantiated.")
            }
        }
    }
}
