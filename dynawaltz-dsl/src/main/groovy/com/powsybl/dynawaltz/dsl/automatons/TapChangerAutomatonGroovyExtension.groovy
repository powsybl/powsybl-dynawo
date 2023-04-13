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
import com.powsybl.dynawaltz.dsl.AbstractPureDynamicModelBuilder
import com.powsybl.dynawaltz.models.TransformerSide
import com.powsybl.dynawaltz.models.automatons.TapChangerAutomaton
import com.powsybl.iidm.network.*

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class TapChangerAutomatonGroovyExtension extends AbstractPureDynamicGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    TapChangerAutomatonGroovyExtension() {
        modelTags = ["TapChangerAutomaton"]
    }

    @Override
    protected TapChangerAutomatonBuilder createBuilder(String tag, Network network) {
        new TapChangerAutomatonBuilder(network)
    }

    static class TapChangerAutomatonBuilder extends AbstractPureDynamicModelBuilder {

        Network network
        Load load
        TransformerSide side = TransformerSide.NONE

        TapChangerAutomatonBuilder(Network network) {
            this.network = network
        }

        void staticId(String staticId) {
            this.load = network.getLoad(staticId)
            if (load == null) {
                throw new DslException("Load static id unknown: " + staticId)
            }
        }

        void side(TransformerSide side) {
            this.side = side
        }

        @Override
        TapChangerAutomaton build() {
            checkData()
            new TapChangerAutomaton(dynamicModelId, parameterSetId, load, side)
        }
    }
}
