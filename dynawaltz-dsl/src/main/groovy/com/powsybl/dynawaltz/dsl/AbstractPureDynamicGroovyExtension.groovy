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
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractPureDynamicGroovyExtension<T> {

    protected List<String> modelTags

    abstract protected ModelBuilder<T> createBuilder(Network network)

    String getName() {
        DynaWaltzProvider.NAME
    }

    void load(Binding binding, Consumer<T> consumer) {
        modelTags.forEach {
            binding.setVariable(it, { Closure<Void> closure ->
                def cloned = closure.clone()
                ModelBuilder<T> builder = createBuilder(binding.getVariable("network") as Network)
                cloned.delegate = builder
                cloned()
                builder.build()?.tap {
                    consumer.accept(it)
                }
            })
        }
    }

}
