/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl

import com.google.auto.service.AutoService
import com.powsybl.commons.report.ReportNode
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.DynaWaltzProvider
import com.powsybl.dynawaltz.builders.BuilderConfig
import com.powsybl.dynawaltz.builders.ModelBuilder
import com.powsybl.dynawaltz.builders.ModelConfigsHandler
import com.powsybl.iidm.network.Network

import java.util.function.Consumer

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(DynamicModelGroovyExtension.class)
class DynaWaltzDynamicModelGroovyExtension implements DynamicModelGroovyExtension {

    private final List<BuilderConfig> builderConfigs

    DynaWaltzDynamicModelGroovyExtension() {
        builderConfigs = ModelConfigsHandler.getInstance().getBuilderConfigs()
    }

    @Override
    String getName() {
        DynaWaltzProvider.NAME
    }

    @Override
    List<String> getModelNames() {
        builderConfigs.stream().flatMap { it -> it.modelInfos.stream()}.map { i -> i.formattedInfo()}.toList() as List<String>
    }


    @Override
    void load(Binding binding, Consumer<DynamicModel> consumer, ReportNode reportNode) {
        builderConfigs.forEach {conf ->
            conf.modelInfos.forEach { modelInfo ->
                def modelName = modelInfo.name()
                binding.setVariable(modelName , { Closure<Void> closure ->
                    def cloned = closure.clone()
                    ModelBuilder<DynamicModel> builder = conf.builderConstructor
                            .createBuilder(binding.getVariable("network") as Network, modelName, DslReports.createModelBuilderReportNode(reportNode, modelName))
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
