/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.lines

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.DynaWaltzProvider
import com.powsybl.dynawaltz.models.buses.StandardBus
import com.powsybl.dynawaltz.models.lines.StandardLine
import com.powsybl.iidm.network.Branch

import java.util.function.Consumer

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>Line</pre> keyword to the DSL
 *
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LineGroovyExtension implements DynamicModelGroovyExtension {

    static class LineSpec {
        String dynamicModelId
        String staticId
        String parameterSetId
        Branch.Side side

        void dynamicModelId(String dynamicModelId) {
            this.dynamicModelId = dynamicModelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void parameterSetId(String parameterSetId) {
            this.parameterSetId = parameterSetId
        }

        void side(Branch.Side side) {
            this.side = side
        }
    }

    String getName() {
        return DynaWaltzProvider.NAME
    }
    
    void load(Binding binding, Consumer<DynamicModel> consumer) {
        binding.Line = { Closure<Void> closure ->
            def cloned = closure.clone()
            LineSpec lineSpec = new LineSpec()
    
            cloned.delegate = lineSpec
            cloned()

            if (!lineSpec.staticId) {
                throw new DslException("'staticId' field is not set");
            }
            if (!lineSpec.parameterSetId) {
                throw new DslException("'parameterSetId' field is not set")
            }

            String dynamicModelId = lineSpec.dynamicModelId ? lineSpec.dynamicModelId : lineSpec.staticId
            Branch.Side side = lineSpec.side ? lineSpec.side : Branch.Side.ONE
            consumer.accept(new StandardLine(dynamicModelId, lineSpec.staticId, lineSpec.parameterSetId, side))
        }
    }

}
