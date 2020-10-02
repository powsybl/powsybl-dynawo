/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dynamicmodels

import java.util.function.Consumer

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawo.DynawoProvider

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>LoadAlphaBeta</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadAlphaBetaGroovyExtension implements DynamicModelGroovyExtension {

    static class LoadAlphaBetaSpec {
        String dynamicModelId
        String staticId
        String parameterSetId

        void dynamicModelId(String dynamicModelId) {
            this.dynamicModelId = dynamicModelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void parameterSetId(String parameterSetId) {
            this.parameterSetId = parameterSetId
        }
    }

    String getName() {
        return DynawoProvider.NAME
    }
    
    void load(Binding binding, Consumer<DynamicModel> consumer) {
        binding.LoadAlphaBeta = { Closure<Void> closure ->
            def cloned = closure.clone()
            LoadAlphaBetaSpec loadAlphaBetaSpec = new LoadAlphaBetaSpec()
    
            cloned.delegate = loadAlphaBetaSpec
            cloned()

            if (!loadAlphaBetaSpec.staticId) {
                throw new DslException("'staticId' field is not set");
            }
            if (!loadAlphaBetaSpec.parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }

            String dynamicModelId = loadAlphaBetaSpec.dynamicModelId ? loadAlphaBetaSpec.dynamicModelId : loadAlphaBetaSpec.staticId
            consumer.accept(new LoadAlphaBeta(dynamicModelId, loadAlphaBetaSpec.staticId, loadAlphaBetaSpec.parameterSetId))
        }
    }

}
