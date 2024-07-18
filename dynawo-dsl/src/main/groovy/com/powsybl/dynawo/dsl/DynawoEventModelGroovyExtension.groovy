/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.dsl

import com.google.auto.service.AutoService
import com.powsybl.commons.report.ReportNode
import com.powsybl.dynamicsimulation.EventModel
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension
import com.powsybl.dynawo.DynawoSimulationProvider
import com.powsybl.dynawo.builders.EventBuilderConfig
import com.powsybl.dynawo.builders.ModelBuilder
import com.powsybl.dynawo.builders.ModelConfigsHandler
import com.powsybl.iidm.network.Network

import java.util.function.Consumer

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(EventModelGroovyExtension.class)
class DynawoEventModelGroovyExtension implements EventModelGroovyExtension {

    private final List<EventBuilderConfig> builderConfigs

    DynawoEventModelGroovyExtension() {
        builderConfigs = ModelConfigsHandler.getInstance().getEventBuilderConfigs()
    }

    @Override
    String getName() {
        DynawoSimulationProvider.NAME
    }

    List<String> getModelNames() {
        builderConfigs.collect {it.eventModelInfo.formattedInfo()}
    }

    @Override
    void load(Binding binding, Consumer<EventModel> consumer, ReportNode reportNode) {
        builderConfigs.forEach {
            binding.setVariable(it.eventModelInfo.name(), { Closure<Void> closure ->
                def cloned = closure.clone()
                ModelBuilder<EventModel> builder = it.builderConstructor.createBuilder(
                        binding.getVariable("network") as Network,
                        DslReports.createModelBuilderReportNode(reportNode, it.eventModelInfo.name()))
                cloned.delegate = builder
                cloned()
                builder.build()?.tap {
                    consumer.accept(it)
                }
            })
        }
    }
}
