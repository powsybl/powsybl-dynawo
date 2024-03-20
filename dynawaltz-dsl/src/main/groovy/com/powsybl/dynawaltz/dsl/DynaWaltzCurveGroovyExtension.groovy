/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl

import com.google.auto.service.AutoService
import com.powsybl.commons.report.ReportNode
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.Curve
import com.powsybl.dynamicsimulation.groovy.CurveGroovyExtension

import com.powsybl.dynawaltz.DynaWaltzCurve
import com.powsybl.dynawaltz.DynaWaltzProvider

import java.util.function.Consumer

/**
 * An implementation of {@link CurveGroovyExtension} that adds the <pre>curve</pre> keyword to the DSL
 *
 * @author Mathieu Bague {@literal <mathieu.bague@rte-france.com>}
 */
@AutoService(CurveGroovyExtension.class)
class DynaWaltzCurveGroovyExtension implements CurveGroovyExtension {

    /**
     * A curve for <pre>DynaWaltz</pre> can be defined in DSL using {@code staticId} and {@code variable} or {@code dynamicModelId} and {@code variable}.
     * Definition with {@code staticId} and {@code variable} are used when no explicit dynamic component exists (buses).
     * <pre>DynaWaltz</pre> expects {@code dynamicModelId} = “NETWORK” for these variables.
     */
    static class CurvesSpec {
        String dynamicModelId
        String staticId
        String[] variables

        void dynamicModelId(String dynamicModelId) {
            this.dynamicModelId = dynamicModelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void variables(String[] variables) {
            this.variables = variables
        }

        void variable(String variable) {
            this.variables = [variable]
        }
    }

    @Override
    String getName() {
        DynaWaltzProvider.NAME
    }

    DynaWaltzCurve dynawoCurve(CurvesSpec curveSpec, Consumer<Curve> consumer) {
        
        if (curveSpec.staticId && curveSpec.dynamicModelId) {
            throw new DslException("Both staticId and dynamicModelId are defined")
        }
        if (!curveSpec.variables) {
            throw new DslException("'variables' field is not set")
        }
        if (curveSpec.variables.length == 0) {
            throw new DslException("'variables' field is empty")
        }

        for (String variable : curveSpec.variables) {
            if (curveSpec.staticId) {
                consumer.accept(new DynaWaltzCurve("NETWORK", curveSpec.staticId + "_" + variable))
            } else {
                consumer.accept(new DynaWaltzCurve(curveSpec.dynamicModelId, variable))
            }
        }
    }

    @Override
    void load(Binding binding, Consumer<Curve> consumer, ReportNode reportNode) {
        binding.curve = { Closure<Void> closure ->
            def cloned = closure.clone()
            CurvesSpec curveSpec = new CurvesSpec()

            cloned.delegate = curveSpec
            cloned()

            dynawoCurve(curveSpec, consumer)
        }

        binding.curves = { Closure<Void> closure ->
            def cloned = closure.clone()
            CurvesSpec curvesSpec = new CurvesSpec()

            cloned.delegate = curvesSpec
            cloned()

            dynawoCurve(curvesSpec, consumer)
        }
    }

}
