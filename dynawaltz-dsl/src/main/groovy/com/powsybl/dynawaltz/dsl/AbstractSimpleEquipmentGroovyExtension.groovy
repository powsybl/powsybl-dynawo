/**
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
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractSimpleEquipmentGroovyExtension<T> {

    protected EquipmentConfig equipmentConfig

    AbstractSimpleEquipmentGroovyExtension(String modelTag) {
        equipmentConfig = new EquipmentConfig(modelTag)
    }


    abstract protected ModelBuilder<T> createBuilder(Network network, EquipmentConfig equipmentConfig)

    String getName() {
        DynaWaltzProvider.NAME
    }

    List<String> getModelNames() {
        List.of(equipmentConfig.lib)
    }

    void load(Binding binding, Consumer<T> consumer) {
        binding.setVariable(equipmentConfig.lib, { Closure<Void> closure ->
            def cloned = closure.clone()
            ModelBuilder<T> builder = createBuilder(binding.getVariable("network") as Network, equipmentConfig)
            cloned.delegate = builder
            cloned()
            builder.build()?.tap {
                consumer.accept(it)
            }
        })
    }
}
