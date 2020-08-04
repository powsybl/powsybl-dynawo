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

    static class ParametersSpec {
        Double load_alpha
        Double load_beta

        void load_alpha(double load_alpha) {
            this.load_alpha = load_alpha
        }

        void load_beta(double load_beta) {
            this.load_beta = load_beta
        }
    }

    static class LoadAlphaBetaSpec {
        String dynamicModelId
        String staticId
        String parameterSetId
        
        final ParametersSpec parametersSpec = new ParametersSpec()

        void dynamicModelId(String dynamicModelId) {
            this.dynamicModelId = dynamicModelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void parameterSetId(String parameterSetId) {
            this.parameterSetId = parameterSetId
        }

        void parameters(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = parametersSpec
            cloned()
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
                throw new DslException("'staticId' field is not set");
            }
            if (!loadAlphaBetaSpec.parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }

            //LoadAlphaBeta.Parameters parameters = LoadAlphaBeta.Parameters.load(parametersDB, loadAlphaBetaSpec.parameterSetId)
            if (loadAlphaBetaSpec.parametersSpec.load_alpha) {
                //parameters.setLoadAlpha(loadAlphaBetaSpec.parametersSpec.load_alpha)
            }
            if (loadAlphaBetaSpec.parametersSpec.load_beta) {
                //parameters.setLoadBeta(loadAlphaBetaSpec.parametersSpec.load_beta)
            }

            String dynamicModelId = loadAlphaBetaSpec.dynamicModelId ? loadAlphaBetaSpec.dynamicModelId : loadAlphaBetaSpec.staticId
            consumer.accept(new LoadAlphaBeta(dynamicModelId, loadAlphaBetaSpec.staticId, loadAlphaBetaSpec.parameterSetId))
        }
    }

}
