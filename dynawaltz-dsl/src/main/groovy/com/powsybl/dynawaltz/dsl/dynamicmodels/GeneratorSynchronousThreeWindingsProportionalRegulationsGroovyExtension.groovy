/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.dynamicmodels

import java.util.function.Consumer

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension

import com.powsybl.dynawaltz.dynamicmodels.GeneratorSynchronousThreeWindingsProportionalRegulations

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>GeneratorSynchronousThreeWindingsProportionalRegulations</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorSynchronousThreeWindingsProportionalRegulationsGroovyExtension extends GeneratorModelGroovyExtension {

    void load(Binding binding, Consumer<DynamicModel> consumer) {
        binding.GeneratorSynchronousThreeWindingsProportionalRegulations = { Closure<Void> closure ->
            def cloned = closure.clone()
            GeneratorModelSpec generatorModelSpec = new GeneratorModelSpec()

            cloned.delegate = generatorModelSpec
            cloned()

            if (!generatorModelSpec.staticId) {
                throw new DslException("'staticId' field is not set")
            }
            if (!generatorModelSpec.parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }

            String dynamicModelId = generatorModelSpec.dynamicModelId ? generatorModelSpec.dynamicModelId : generatorModelSpec.staticId
            consumer.accept(new GeneratorSynchronousThreeWindingsProportionalRegulations(dynamicModelId, generatorModelSpec.staticId, generatorModelSpec.parameterSetId))
        }
    }

}
