/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl

import com.powsybl.dynawaltz.DynaWaltzProvider
import com.powsybl.iidm.network.Network

import java.util.function.Consumer

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractEquipmentGroovyExtension<T> {

    protected static final String MODELS_CONFIG = "models.cfg"
    protected static final String MODELS_PROPERTIES = "properties"

    protected List<EquipmentConfig> equipmentConfigs

    AbstractEquipmentGroovyExtension(String modelTag) {
        ConfigSlurper config = new ConfigSlurper()
        equipmentConfigs = config.parse(this.getClass().getClassLoader().getResource(MODELS_CONFIG)).get(modelTag).collect {
            def lib = it.key
            (it.value.containsKey(MODELS_PROPERTIES))
                    ? new EquipmentConfig(lib, it.value.get(MODELS_PROPERTIES).collect{it.toUpperCase() as EnumEquipmentProperty})
                    : new EquipmentConfig(lib)
        }
    }

    abstract protected ModelBuilder<T> createBuilder(Network network, EquipmentConfig equipmentConfig);

    String getName() {
        return DynaWaltzProvider.NAME
    }

    void load(Binding binding, Consumer<T> consumer) {
        equipmentConfigs.forEach {
            binding.setVariable(it.lib, { Closure<Void> closure ->
                def cloned = closure.clone()
                ModelBuilder<T> builder = createBuilder(binding.getVariable("network") as Network, it)
                cloned.delegate = builder
                cloned()
                consumer.accept(builder.build())
            })
        }
    }

}
