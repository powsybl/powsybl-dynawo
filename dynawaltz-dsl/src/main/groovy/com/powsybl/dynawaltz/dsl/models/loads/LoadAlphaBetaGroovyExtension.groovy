/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.loads

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.dsl.AbstractPowsyblDynawoGroovyExtension
import com.powsybl.dynawaltz.models.loads.LoadAlphaBeta

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>LoadAlphaBeta</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadAlphaBetaGroovyExtension extends AbstractPowsyblDynawoGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    LoadAlphaBetaGroovyExtension() {
        modelTags = ["LoadAlphaBeta"]
    }

    @Override
    protected LoadAlphaBetaBuilder createBuilder(String currentTag) {
        new LoadAlphaBetaBuilder()
    }

    static class LoadAlphaBetaBuilder extends AbstractDynamicModelBuilder {
        @Override
        LoadAlphaBeta build() {
            checkData()
            new LoadAlphaBeta(dynamicModelId, staticId, parameterSetId)
        }
    }
}
