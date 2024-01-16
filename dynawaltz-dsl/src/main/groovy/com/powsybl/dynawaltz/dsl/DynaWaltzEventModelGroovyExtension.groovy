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
import com.powsybl.dynawaltz.builders.EventBuilderConfig
import com.powsybl.dynawaltz.builders.ModelBuilder
import com.powsybl.dynawaltz.builders.ModelConfigsSingleton
import com.powsybl.iidm.network.Network

import java.util.function.Consumer
/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(EventModelGroovyExtension.class)
class DynaWaltzEventModelGroovyExtension implements EventModelGroovyExtension {

    private final List<EventBuilderConfig> builderConfigs

    DynaWaltzEventModelGroovyExtension() {
        builderConfigs = ModelConfigsSingleton.getInstance().getEventBuilderConfigs()
    }

    @Override
    String getName() {
        DynaWaltzProvider.NAME
    }

    List<String> getModelNames() {
        builderConfigs.collect {it.tag}
    }

    @Override
    void load(Binding binding, Consumer<EventModel> consumer, Reporter reporter) {
        builderConfigs.forEach {
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
