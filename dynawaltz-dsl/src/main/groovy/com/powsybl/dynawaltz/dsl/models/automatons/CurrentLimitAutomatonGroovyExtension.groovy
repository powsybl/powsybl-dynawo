/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.automatons

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

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>CurrentLimitAutomaton</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class CurrentLimitAutomatonGroovyExtension extends AbstractPureDynamicGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    CurrentLimitAutomatonGroovyExtension() {
        modelTags = ["CurrentLimitAutomaton"]
    }

    @Override
    protected CurrentLimitAutomatonBuilder createBuilder() {
        new CurrentLimitAutomatonBuilder()
    }

    static class CurrentLimitAutomatonBuilder extends AbstractPureDynamicModelBuilder {

        String lineStaticId
        Side side

        void staticId(String staticId) {
            this.lineStaticId = staticId
        }

        void side(Branch.Side side) {
            this.side = SideConverter.convert(side)
        }

        @Override
        void checkData() {
            super.checkData()
            if (!side) {
                throw new DslException("'side' field is not set")
            }
            if (!dynamicModelId) {
                dynamicModelId = lineStaticId
            }
        }

        @Override
        CurrentLimitAutomaton build() {
            checkData()
            new CurrentLimitAutomaton(dynamicModelId, lineStaticId, parameterSetId, side)
        }
    }
}
