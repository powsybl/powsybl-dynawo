/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.automatons

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicModelGroovyExtension
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterIAutomaton
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class PhaseShifterIAutomatonGroovyExtension extends AbstractPureDynamicModelGroovyExtension<PhaseShifterIAutomaton> {

    PhaseShifterIAutomatonGroovyExtension() {
        modelTags = ["PhaseShifterI"]
    }

    @Override
    protected PhaseShifterPAutomatonBuilder createBuilder(Network network) {
        new PhaseShifterPAutomatonBuilder(network)
    }

    static class PhaseShifterPAutomatonBuilder extends AbstractPhaseShifterModelBuilder {

        PhaseShifterPAutomatonBuilder(Network network) {
            super(network)
        }

        @Override
        PhaseShifterIAutomaton build() {
            checkData()
            new PhaseShifterIAutomaton(dynamicModelId, transformer, parameterSetId)
        }
    }
}
