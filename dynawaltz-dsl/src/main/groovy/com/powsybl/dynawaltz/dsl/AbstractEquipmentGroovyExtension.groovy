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

    protected static final String MODELS_CONFIG = "models.json"

    protected final List<EquipmentConfig> equipmentConfigs

    AbstractEquipmentGroovyExtension(String modelTag) {
        this(modelTag, AbstractEquipmentGroovyExtension.class.getClassLoader().getResource(MODELS_CONFIG))
    }

    protected AbstractEquipmentGroovyExtension(String modelTag, URL modelConfigUrl) {
        equipmentConfigs = ModelsSlurper.instance.getEquipmentConfigs(modelConfigUrl, modelTag)
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
