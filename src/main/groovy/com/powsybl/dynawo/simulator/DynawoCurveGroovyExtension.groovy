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
        String[] variables

        void variables(String[] variables) {
            this.variables = variables
        }
    }

    void load(Binding binding, Consumer<Curve> consumer) {
        binding.curve = { String id, Closure<Void> closure ->
            def cloned = closure.clone()
            CurveSpec curveSpec = new CurveSpec()

            cloned.delegate = curveSpec
            cloned()

            if (!curveSpec.variables) {
                throw new DslException("'variables' field is not set")
            }
            if (curveSpec.variables.length == 0) {
                throw new DslException("'variables' field is empty")
            }

            Curve curve = new CurveImpl(id, Arrays.asList(curveSpec.variables))
            consumer.accept(curve)
        }
    }

}
