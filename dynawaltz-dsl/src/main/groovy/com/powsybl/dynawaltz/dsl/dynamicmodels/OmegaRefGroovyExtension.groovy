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

import com.powsybl.dynawaltz.DynaWaltzProvider
import com.powsybl.dynawaltz.dynamicmodels.OmegaRef

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>DYNModelOmegaRef</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class OmegaRefGroovyExtension implements DynamicModelGroovyExtension {

    static class OmegaRefSpec {
        String generatorDynamicModelId

        void generatorDynamicModelId(String generatorDynamicModelId) {
            this.generatorDynamicModelId = generatorDynamicModelId
        }
    }

    String getName() {
        return DynaWaltzProvider.NAME
    }

    void load(Binding binding, Consumer<DynamicModel> consumer) {
        binding.OmegaRef = { Closure<Void> closure ->
            def cloned = closure.clone()
            OmegaRefSpec omegaRefSpec = new OmegaRefSpec()

            cloned.delegate = omegaRefSpec
            cloned()

            if (!omegaRefSpec.generatorDynamicModelId) {
                throw new DslException("'generatorDynamicModelId' field is not set")
            }

            consumer.accept(new OmegaRef(omegaRefSpec.generatorDynamicModelId));
        }
    }
}
