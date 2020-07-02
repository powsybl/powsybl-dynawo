/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator

import java.util.function.Consumer

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawo.dyd.LoadAlphaBeta

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>LoadAlphaBeta</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadAlphaBetaGroovyExtension implements DynamicModelGroovyExtension {

    static class LoadAlphaBetaSpec {
        String modelId
        String staticId
        String parameterSetId

        void modelId(String modelId) {
            this.modelId = modelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void parameterSetId(String parameterSetId) {
            this.parameterSetId = parameterSetId
        }
    }

    String getName() {
        return "dynawo"
    }
    
    void load(Binding binding, Consumer<DynamicModel> consumer) {
        binding.LoadAlphaBeta = { Closure<Void> closure ->
            def cloned = closure.clone()
            LoadAlphaBetaSpec loadAlphaBetaSpec = new LoadAlphaBetaSpec()

            cloned.delegate = loadAlphaBetaSpec
            cloned()

            if (!loadAlphaBetaSpec.staticId) {
                throw new DslException("'staticId' field is not set")
            }
            if (!loadAlphaBetaSpec.parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }

            String modelId = loadAlphaBetaSpec.modelId ? loadAlphaBetaSpec.modelId : 'BBM_' + loadAlphaBetaSpec.staticId
            consumer.accept(new LoadAlphaBeta(modelId, loadAlphaBetaSpec.staticId, loadAlphaBetaSpec.parameterSetId))
        }
    }

}
