/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.buses

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.DynaWaltzProvider
import com.powsybl.dynawaltz.models.buses.StandardBus

import java.util.function.Consumer

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>Bus</pre> keyword to the DSL
 *
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class BusGroovyExtension implements DynamicModelGroovyExtension {

    static class BusSpec {
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
        binding.Bus = { Closure<Void> closure ->
            def cloned = closure.clone()
            BusSpec busSpec = new BusSpec()
    
            cloned.delegate = busSpec
            cloned()

            if (!busSpec.staticId) {
                throw new DslException("'staticId' field is not set");
            }
            if (!busSpec.parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }

            String dynamicModelId = busSpec.dynamicModelId ? busSpec.dynamicModelId : busSpec.staticId
            consumer.accept(new StandardBus(dynamicModelId, busSpec.staticId, busSpec.parameterSetId))
        }
    }

}
