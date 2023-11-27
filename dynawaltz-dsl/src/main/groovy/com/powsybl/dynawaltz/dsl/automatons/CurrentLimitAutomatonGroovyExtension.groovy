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
import com.powsybl.dynawaltz.dsl.DslEquipment
import com.powsybl.dynawaltz.dsl.Reporters
import com.powsybl.dynawaltz.dsl.builders.AbstractPureDynamicModelBuilder
import com.powsybl.dynawaltz.models.Side
import com.powsybl.dynawaltz.models.automatons.CurrentLimitAutomaton
import com.powsybl.dynawaltz.models.utils.SideConverter
import com.powsybl.iidm.network.Branch
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.TwoSides

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
    protected CurrentLimitAutomatonBuilder createBuilder(Network network, Reporter reporter) {
        new CurrentLimitAutomatonBuilder(network, getLib(), reporter)
    }

    protected String getLib() {
        "CurrentLimitAutomaton"
    }

    static class CurrentLimitAutomatonBuilder extends AbstractPureDynamicModelBuilder {

        protected final DslEquipment<Branch> iMeasurement
        protected Side iMeasurementSide
        protected final DslEquipment<Branch> controlledEquipment

        CurrentLimitAutomatonBuilder(Network network, String lib, Reporter reporter) {
            super(network, lib, reporter)
            iMeasurement = new DslEquipment<>("Quadripole", "iMeasurement")
            controlledEquipment = new DslEquipment<>("Quadripole", "controlledQuadripole")
        }

        void iMeasurement(String staticId) {
            iMeasurement.addEquipment(staticId, network::getBranch)
        }

        void iMeasurementSide(TwoSides side) {
            this.iMeasurementSide = SideConverter.convert(side)
        }

        void controlledQuadripole(String staticId) {
            controlledEquipment.addEquipment(staticId, network::getBranch)
        }

        @Override
        void checkData() {
            super.checkData()
            isInstantiable &= controlledEquipment.checkEquipmentData(reporter)
            isInstantiable &= iMeasurement.checkEquipmentData(reporter)
            if (!iMeasurementSide) {
                Reporters.reportFieldNotSet(reporter, "iMeasurementSide")
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
