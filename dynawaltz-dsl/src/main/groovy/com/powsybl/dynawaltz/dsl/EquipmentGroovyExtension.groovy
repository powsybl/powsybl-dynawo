/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl

import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.DynaWaltzProvider
import com.powsybl.dynawaltz.builders.DynamicModelBuilderUtils
import com.powsybl.dynawaltz.builders.EquipmentConfig
import com.powsybl.dynawaltz.builders.ModelBuilder
import com.powsybl.iidm.network.Network
import org.apache.commons.lang3.tuple.Pair

import java.util.function.Consumer
/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class EquipmentGroovyExtension implements DynamicModelGroovyExtension {

    protected final List<Pair<DynamicModelBuilderUtils.ModelBuilderConstructorFull, Collection<EquipmentConfig>>> builderConstructors

    EquipmentGroovyExtension() {
        builderConstructors = DynamicModelBuilderUtils.getAllBuildersConstructors()
    }

    @Override
    String getName() {
        DynaWaltzProvider.NAME
    }

    @Override
    List<String> getModelNames() {
        builderConstructors.stream().flatMap { it -> it.right.lib}.toList() as List<String>
    }

    @Override
    void load(Binding binding, Consumer<DynamicModel> consumer, Reporter reporter) {
        builderConstructors.forEach {
            it.right.forEach {eq ->
                binding.setVariable(eq.lib, { Closure<Void> closure ->
                    def cloned = closure.clone()
                    ModelBuilder<DynamicModel> builder = it.left
                            .createBuilder(binding.getVariable("network") as Network, eq, Reporters.createModelBuilderReporter(reporter, eq.lib))
                    cloned.delegate = builder
                    cloned()
                    builder.build()?.tap {
                        consumer.accept(it)
                    }
                })
            }
        }
    }
}
