/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.DynaWaltzProvider
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronous

import java.util.function.Consumer

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorSynchronousModelGroovyExtension extends GeneratorModelGroovyExtension {

    private static final String SYNCHRONOUS_GENERATORS_LIBS = "synchronousGeneratorsLibs";

    void load(Binding binding, Consumer<DynamicModel> consumer) {
        ConfigSlurper config = new ConfigSlurper()
        def cfg = config.parse(this.getClass().getClassLoader().getResource(GENERATORS_CONFIG)).get(SYNCHRONOUS_GENERATORS_LIBS)
        for (String gen : cfg.keySet()) {
            binding.setVariable(gen, generatorClosure(consumer, gen))
        }
    }

    def generatorClosure = {
        Consumer<DynamicModel> consumer, String generator ->
        {
            Closure<Void> closure -> {
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
                consumer.accept(new GeneratorSynchronous(dynamicModelId, generatorModelSpec.staticId, generatorModelSpec.parameterSetId, generator))
            }
        }
    }
}
