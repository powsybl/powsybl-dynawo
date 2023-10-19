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
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterIAutomaton
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class PhaseShifterIAutomatonGroovyExtension extends AbstractPureDynamicGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    private static final String LIB = "PhaseShifterI"

    PhaseShifterIAutomatonGroovyExtension() {
        modelTags = [LIB]
    }

    @Override
    protected PhaseShifterPAutomatonBuilder createBuilder(Network network, Reporter reporter) {
        new PhaseShifterPAutomatonBuilder(network, LIB, reporter)
    }

    static class PhaseShifterPAutomatonBuilder extends AbstractPhaseShifterModelBuilder {

        PhaseShifterPAutomatonBuilder(Network network, String lib, Reporter reporter) {
            super(network, lib, reporter)
        }

        @Override
        PhaseShifterIAutomaton build() {
            isInstantiable() ? new PhaseShifterIAutomaton(dynamicModelId, dslTransformer.equipment, parameterSetId) : null
        }
    }
}
