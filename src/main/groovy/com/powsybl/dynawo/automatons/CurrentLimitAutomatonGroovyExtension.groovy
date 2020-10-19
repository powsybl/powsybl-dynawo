/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.automatons

import java.util.function.Consumer

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawo.DynawoProvider
import com.powsybl.dynawo.automatons.CurrentLimitAutomaton

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>CurrentLimitAutomaton</pre> keyword to the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class CurrentLimitAutomatonGroovyExtension implements DynamicModelGroovyExtension {

    static class CurrentLimitAutomatonSpec {
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
        binding.CurrentLimitAutomaton = { Closure<Void> closure ->
            def cloned = closure.clone()
            CurrentLimitAutomatonSpec currentLimitAutomatonSpec = new CurrentLimitAutomatonSpec()
    
            cloned.delegate = currentLimitAutomatonSpec
            cloned()

            if (!currentLimitAutomatonSpec.staticId) {
                throw new DslException("'staticId' field is not set");
            }
            if (!currentLimitAutomatonSpec.parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }

            String dynamicModelId = currentLimitAutomatonSpec.dynamicModelId ? currentLimitAutomatonSpec.dynamicModelId : currentLimitAutomatonSpec.staticId
            consumer.accept(new CurrentLimitAutomaton(dynamicModelId, currentLimitAutomatonSpec.staticId, currentLimitAutomatonSpec.parameterSetId))
        }
    }

}
