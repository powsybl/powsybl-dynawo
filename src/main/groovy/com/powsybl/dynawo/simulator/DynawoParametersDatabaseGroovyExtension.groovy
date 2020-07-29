/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator

import java.util.function.Consumer

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawo.simulator.DynawoParametersDatabase

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the dynawo parametersDB to the binding of the DSL
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class DynawoParametersDatabaseGroovyExtension implements DynamicModelGroovyExtension {

    String getName() {
        return "dynawo"
    }

    void load(Binding binding, Consumer<DynamicModel> consumer) {
        binding.parametersDatabase = DynawoParametersDatabase.load();
    }
}