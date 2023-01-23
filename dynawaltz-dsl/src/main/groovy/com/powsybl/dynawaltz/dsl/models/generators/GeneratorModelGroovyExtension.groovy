/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.powsybl.dynawaltz.models.generators.GeneratorSynchronous

import java.util.function.Consumer

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension

import com.powsybl.dynawaltz.DynaWaltzProvider

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorModelGroovyExtension implements DynamicModelGroovyExtension {

    private static final String GENERATORS_CONFIG = "synchronous_generators.cfg";
    private static final String SYNCHRONOUS_GENERATORS_LIBS = "synchronousGeneratorsLibs";

    static class GeneratorModelSpec {
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
        return DynaWaltzProvider.NAME
    }

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

                if (!generatorModelSpec.staticId) {SYNCHRONOUS_GENERATORS_LIBS
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
