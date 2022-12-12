/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.dynamicmodels.loads

import com.powsybl.dynawaltz.dynamicmodels.staticid.staticref.loads.LoadOneTransformer

import java.util.function.Consumer

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.DynaWaltzProvider

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>LoadOneTransformer</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadOneTransformerGroovyExtension implements DynamicModelGroovyExtension {

    static class LoadOneTransformerSpec {
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
        binding.LoadOneTransformer = { Closure<Void> closure ->
            def cloned = closure.clone()
            LoadOneTransformerSpec loadOneTransformerSpec = new LoadOneTransformerSpec()
    
            cloned.delegate = loadOneTransformerSpec
            cloned()

            if (!loadOneTransformerSpec.staticId) {
                throw new DslException("'staticId' field is not set");
            }
            if (!loadOneTransformerSpec.parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }

            String dynamicModelId = loadOneTransformerSpec.dynamicModelId ? loadOneTransformerSpec.dynamicModelId : loadOneTransformerSpec.staticId
            consumer.accept(new LoadOneTransformer(dynamicModelId, loadOneTransformerSpec.staticId, loadOneTransformerSpec.parameterSetId))
        }
    }

}
