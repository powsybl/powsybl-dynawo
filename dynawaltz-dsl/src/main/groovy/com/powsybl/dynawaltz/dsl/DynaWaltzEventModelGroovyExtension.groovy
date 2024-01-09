/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.EventModel
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension
import com.powsybl.dynawaltz.DynaWaltzProvider
import com.powsybl.dynawaltz.builders.EventModelsBuilderUtils
import com.powsybl.dynawaltz.builders.EventModelCategory
import com.powsybl.dynawaltz.builders.ModelBuilder
import com.powsybl.iidm.network.Network

import java.util.function.Consumer
/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(EventModelGroovyExtension.class)
class DynaWaltzEventModelGroovyExtension implements EventModelGroovyExtension {

    private final List<EventModelCategory>  modelConstructors

    DynaWaltzEventModelGroovyExtension() {
        modelConstructors = EventModelsBuilderUtils.eventModelCategories
    }

    @Override
    String getName() {
        DynaWaltzProvider.NAME
    }

    List<String> getModelNames() {
        modelConstructors.collect {it.tag}
    }

    @Override
    void load(Binding binding, Consumer<EventModel> consumer, Reporter reporter) {
        modelConstructors.forEach {
            binding.setVariable(it.tag, { Closure<Void> closure ->
                def cloned = closure.clone()
                ModelBuilder<EventModel> builder = it.builderConstructor.createBuilder(
                        binding.getVariable("network") as Network,
                        Reporters.createModelBuilderReporter(reporter, it.tag))
                cloned.delegate = builder
                cloned()
                builder.build()?.tap {
                    consumer.accept(it)
                }
            })
        }
    }
}
