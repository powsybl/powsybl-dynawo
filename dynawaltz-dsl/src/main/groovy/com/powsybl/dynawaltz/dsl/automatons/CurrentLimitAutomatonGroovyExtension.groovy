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
import com.powsybl.dynawaltz.dsl.DslEquipment
import com.powsybl.dynawaltz.dsl.builders.AbstractPureDynamicModelBuilder
import com.powsybl.dynawaltz.models.Side
import com.powsybl.dynawaltz.models.automatons.CurrentLimitAutomaton
import com.powsybl.dynawaltz.models.utils.SideConverter
import com.powsybl.iidm.network.Branch
import com.powsybl.iidm.network.Network

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>CurrentLimitAutomaton</pre> keyword to the DSL
 *
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
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
        "CurrentLimitAutomaton"
    }

    static class CurrentLimitAutomatonBuilder extends AbstractPureDynamicModelBuilder {

        protected final DslEquipment<Branch> iMeasurement
        protected Side iMeasurementSide
        protected final DslEquipment<Branch> controlledEquipment

        CurrentLimitAutomatonBuilder(Network network, String lib) {
            super(network, lib)
            iMeasurement = new DslEquipment<>("I measurement quadripole", "iMeasurement")
            controlledEquipment = new DslEquipment<>("Controlled quadripole", "controlledQuadripole")
        }

        void iMeasurement(String staticId) {
            iMeasurement.addEquipment(staticId, network::getBranch)
        }

        void iMeasurementSide(Branch.Side side) {
            this.iMeasurementSide = SideConverter.convert(side)
        }

        void controlledQuadripole(String staticId) {
            controlledEquipment.addEquipment(staticId, network::getBranch)
        }

        @Override
        void checkData() {
            super.checkData()
            isInstantiable &= controlledEquipment.checkEquipmentData(LOGGER, getLib())
            isInstantiable &= iMeasurement.checkEquipmentData(LOGGER, getLib())
            if (!iMeasurementSide) {
                LOGGER.warn("${getLib()}: 'iMeasurementSide' field is not set")
                isInstantiable = false
            }
        }

        @Override
        CurrentLimitAutomaton build() {
            isInstantiable() ? new CurrentLimitAutomaton(dynamicModelId, parameterSetId,
                    iMeasurement.equipment, iMeasurementSide, controlledEquipment.equipment, lib)
                    : null
        }
    }
}
