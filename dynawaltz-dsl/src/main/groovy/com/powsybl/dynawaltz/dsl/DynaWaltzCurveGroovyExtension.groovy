/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl

import com.google.auto.service.AutoService
import com.powsybl.commons.report.ReportNode
import com.powsybl.dynamicsimulation.Curve
import com.powsybl.dynamicsimulation.groovy.CurveGroovyExtension
import com.powsybl.dynawaltz.DynaWaltzProvider
import com.powsybl.dynawaltz.curves.DynawoCurvesBuilder

import java.util.function.Consumer
/**
 * An implementation of {@link CurveGroovyExtension} that adds the <pre>curve</pre> keyword to the DSL
 *
 * @author Mathieu Bague {@literal <mathieu.bague@rte-france.com>}
 */
@AutoService(CurveGroovyExtension.class)
class DynaWaltzCurveGroovyExtension implements CurveGroovyExtension {

    @Override
    String getName() {
        DynaWaltzProvider.NAME
    }

    @Override
    void load(Binding binding, Consumer<Curve> consumer, ReportNode reportNode) {
        Closure<Void> closure = { Closure<Void> closure ->
            def cloned = closure.clone()
            DynawoCurvesBuilder curvesBuilder = new DynawoCurvesBuilder(reportNode)
            cloned.delegate = curvesBuilder
            cloned()
            curvesBuilder.add(consumer)
        }
        binding.curves = closure
    }
}
