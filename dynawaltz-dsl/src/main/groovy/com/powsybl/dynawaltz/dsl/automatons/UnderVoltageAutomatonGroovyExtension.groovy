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
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.models.builders.AbstractPureDynamicModelBuilder
import com.powsybl.dynawaltz.models.automatons.UnderVoltageAutomaton
import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class UnderVoltageAutomatonGroovyExtension extends AbstractPureDynamicModelGroovyExtension<UnderVoltageAutomaton> {

    UnderVoltageAutomatonGroovyExtension() {
        modelTags = ["UnderVoltage"]
    }

    @Override
    protected UnderVoltageAutomatonBuilder createBuilder(Network network) {
        new UnderVoltageAutomatonBuilder(network)
    }

    static class UnderVoltageAutomatonBuilder extends AbstractPureDynamicModelBuilder {

        Network network

        Generator generator

        UnderVoltageAutomatonBuilder(Network network) {
            this.network = network
        }

        void generator(String staticId) {
            this.generator = network.getGenerator(staticId)
            if (generator == null) {
                throw new DslException("Generator static id unknown: " + staticId)
            }
        }

        @Override
        UnderVoltageAutomaton build() {
            checkData()
            new UnderVoltageAutomaton(dynamicModelId, parameterSetId, generator)
        }
    }
}
