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
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterPAutomaton
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class PhaseShifterPAutomatonGroovyExtension extends AbstractPureDynamicGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    PhaseShifterPAutomatonGroovyExtension() {
        modelTags = ["PhaseShifterP"]
    }

    @Override
    protected PhaseShifterPAutomatonBuilder createBuilder(String tag, Network network) {
        new PhaseShifterPAutomatonBuilder(network)
    }

    static class PhaseShifterPAutomatonBuilder extends AbstractPhaseShifterModelBuilder {

        PhaseShifterPAutomatonBuilder(Network network) {
            super(network)
        }

        @Override
        PhaseShifterPAutomaton build() {
            checkData()
            new PhaseShifterPAutomaton(dynamicModelId, transformer, parameterSetId)
        }
    }
}