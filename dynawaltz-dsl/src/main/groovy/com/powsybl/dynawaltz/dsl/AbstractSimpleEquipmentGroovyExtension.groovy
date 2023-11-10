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
import com.powsybl.iidm.network.Network

import java.util.function.Consumer

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractSimpleEquipmentGroovyExtension implements DynamicModelGroovyExtension {

    protected EquipmentConfig equipmentConfig

    AbstractSimpleEquipmentGroovyExtension(String modelTag) {
        equipmentConfig = new EquipmentConfig(modelTag)
    }

    abstract protected ModelBuilder<DynamicModel> createBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter)

    @Override
    String getName() {
        DynaWaltzProvider.NAME
    }

    @Override
    List<String> getModelNames() {
        List.of(equipmentConfig.lib)
    }

    @Override
    void load(Binding binding, Consumer<DynamicModel> consumer, Reporter reporter) {
        binding.setVariable(equipmentConfig.lib, { Closure<Void> closure ->
            def cloned = closure.clone()
            ModelBuilder<DynamicModel> builder = createBuilder(binding.getVariable("network") as Network,
                    equipmentConfig,
                    Reporters.createModelBuilderReporter(reporter, equipmentConfig.lib))
            cloned.delegate = builder
            cloned()
            builder.build()?.tap {
                consumer.accept(it)
            }
        })
    }
}
