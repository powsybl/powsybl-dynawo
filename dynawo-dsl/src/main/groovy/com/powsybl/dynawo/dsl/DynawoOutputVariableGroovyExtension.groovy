/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl

import com.google.auto.service.AutoService
import com.powsybl.commons.report.ReportNode
import com.powsybl.dynamicsimulation.OutputVariable
import com.powsybl.dynamicsimulation.groovy.OutputVariableGroovyExtension
import com.powsybl.dynawo.DynawoSimulationProvider
import com.powsybl.dynawo.outputvariables.DynawoOutputVariablesBuilder

import java.util.function.Consumer
/**
 * An implementation of {@link OutputVariableGroovyExtension} that adds the <pre>curve</pre> and <pre>fsv</pre> keywords to the DSL
 *
 * @author Mathieu Bague {@literal <mathieu.bague@rte-france.com>}
 */
@AutoService(OutputVariableGroovyExtension.class)
class DynawoOutputVariableGroovyExtension implements OutputVariableGroovyExtension {

    @Override
    String getName() {
        DynawoSimulationProvider.NAME
    }

    @Override
    void load(Binding binding, Consumer<OutputVariable> consumer, ReportNode reportNode) {
        Closure<Void> closure = { Closure<Void> closure, OutputVariable.OutputType type ->
            def cloned = closure.clone()
            DynawoOutputVariablesBuilder variablesBuilder = new DynawoOutputVariablesBuilder(reportNode).outputType(type)
            cloned.delegate = variablesBuilder
            cloned()
            variablesBuilder.add(consumer)
        }
        binding.curve = c -> closure(c, OutputVariable.OutputType.CURVE)
        binding.fsv = c -> closure(c, OutputVariable.OutputType.FINAL_STATE)
    }
}
