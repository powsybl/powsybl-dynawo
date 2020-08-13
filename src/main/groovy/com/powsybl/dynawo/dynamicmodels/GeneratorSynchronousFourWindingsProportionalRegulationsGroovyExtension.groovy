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

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>GeneratorSynchronousFourWindingsProportionalRegulations</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorSynchronousFourWindingsProportionalRegulationsGroovyExtension implements DynamicModelGroovyExtension {

    static class GeneratorSynchronousFourWindingsProportionalRegulationsSpec {
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
        return "Dynawo"
    }
    
    void load(Binding binding, Consumer<DynamicModel> consumer) {
        binding.GeneratorSynchronousFourWindingsProportionalRegulations = { Closure<Void> closure ->
            def cloned = closure.clone()
            GeneratorSynchronousFourWindingsProportionalRegulationsSpec generatorSynchronousFourWindingsProportionalRegulationsSpec = new GeneratorSynchronousFourWindingsProportionalRegulationsSpec()

            cloned.delegate = generatorSynchronousFourWindingsProportionalRegulationsSpec
            cloned()

            if (!generatorSynchronousFourWindingsProportionalRegulationsSpec.staticId) {
                throw new DslException("'staticId' field is not set")
            }
            if (!generatorSynchronousFourWindingsProportionalRegulationsSpec.parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }

            String dynamicModelId = generatorSynchronousFourWindingsProportionalRegulationsSpec.dynamicModelId ? generatorSynchronousFourWindingsProportionalRegulationsSpec.dynamicModelId : generatorSynchronousFourWindingsProportionalRegulationsSpec.staticId
            consumer.accept(new GeneratorSynchronousFourWindingsProportionalRegulations(dynamicModelId, generatorSynchronousFourWindingsProportionalRegulationsSpec.staticId, generatorSynchronousFourWindingsProportionalRegulationsSpec.parameterSetId))
        }
    }

}
