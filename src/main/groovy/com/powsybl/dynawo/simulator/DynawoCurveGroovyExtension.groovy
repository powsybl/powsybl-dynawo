/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.Curve
import com.powsybl.dynamicsimulation.groovy.CurveGroovyExtension
import com.powsybl.iidm.network.BusbarSection
import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.HvdcLine
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.Line
import com.powsybl.iidm.network.ShuntCompensator
import com.powsybl.iidm.network.StaticVarCompensator
import com.powsybl.iidm.network.TwoWindingsTransformer

import java.util.function.Consumer

/**
 * An implementation of {@link CurveGroovyExtension} that adds the <pre>curve</pre> keyword to the DSL
 *
 * @author Mathieu Bague <mathieu.bague@rte-france.com>
 */
@AutoService(CurveGroovyExtension.class)
class DynawoCurveGroovyExtension implements CurveGroovyExtension {

    static class CurveSpec {
        String modelId
        String staticId
        String variable

        void modelId(String modelId) {
            this.modelId = modelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void variable(String variable) {
            this.variable = variable
        }
    }

    static class CurvesSpec {
        String modelId
        String staticId
        String[] variables

        void modelId(String modelId) {
            this.modelId = modelId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }

        void variables(String[] variables) {
            this.variables = variables
        }
    }

    String getName() {
        return "dynawo";
    }

    void load(Binding binding, Consumer<Curve> consumer) {
        binding.curve = { Closure<Void> closure ->
            def cloned = closure.clone()
            CurveSpec curveSpec = new CurveSpec()

            cloned.delegate = curveSpec
            cloned()

            if (curveSpec.staticId && curveSpec.modelId) {
                throw new DslException("Both staticId and modelId are defined");
            }
            if (!curveSpec.variable) {
                throw new DslException("'variable' field is not set")
            }
            if (curveSpec.staticId) {
                consumer.accept(new DynawoCurve("NETWORK", curveSpec.staticId + "_" + curveSpec.variable));
            } else {
                consumer.accept(new DynawoCurve(curveSpec.modelId, curveSpec.variable));
            }
        }

        binding.curves = { Closure<Void> closure ->
            def cloned = closure.clone()
            CurvesSpec curvesSpec = new CurvesSpec()

            cloned.delegate = curvesSpec
            cloned()

            if (curvesSpec.staticId && curvesSpec.modelId) {
                throw new DslException("Both staticId and modelId are defined");
            }
            if (!curvesSpec.variables) {
                throw new DslException("'variables' field is not set")
            }
            if (curvesSpec.variables.length == 0) {
                throw new DslException("'variables' field is empty")
            }

            for (String variable : curvesSpec.variables) {
                if (curvesSpec.staticId) {
                    consumer.accept(new DynawoCurve("NETWORK", curvesSpec.staticId + "_" + variable));
                } else {
                    consumer.accept(new DynawoCurve(curvesSpec.modelId, variable));
                }
            }
        }
    }

}
