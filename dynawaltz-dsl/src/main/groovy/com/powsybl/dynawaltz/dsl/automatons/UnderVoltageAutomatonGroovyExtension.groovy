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
import com.powsybl.dynawaltz.models.automatons.UnderVoltageAutomaton
import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class UnderVoltageAutomatonGroovyExtension extends AbstractPureDynamicGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    private static final String LIB = "UnderVoltage"

    UnderVoltageAutomatonGroovyExtension() {
        modelTags = [LIB]
    }

    @Override
    protected UnderVoltageAutomatonBuilder createBuilder(Network network) {
        new UnderVoltageAutomatonBuilder(network, LIB)
    }

    static class UnderVoltageAutomatonBuilder extends AbstractPureDynamicModelBuilder {

        protected final DslEquipment<Generator> dslGenerator

        UnderVoltageAutomatonBuilder(Network network, String lib) {
            super(network, lib)
            dslGenerator = new DslEquipment<>(IdentifiableType.GENERATOR, "generator")
        }

        void generator(String staticId) {
            dslGenerator.addEquipment(staticId, network::getGenerator)
        }

        @Override
        protected void checkData() {
            super.checkData()
            isInstantiable &= dslGenerator.checkEquipmentData(LOGGER, getLib())
        }

        @Override
        UnderVoltageAutomaton build() {
            isInstantiable() ? new UnderVoltageAutomaton(dynamicModelId, parameterSetId, dslGenerator.equipment) : null
        }
    }
}
