/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.models.generators.SynchronizedGenerator
import com.powsybl.dynawaltz.models.generators.SynchronizedGeneratorControllable
import com.powsybl.iidm.network.Network

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class SynchronizedGeneratorGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    private static final String SYNCHRONIZED_GENERATORS = "synchronizedGenerators"

    SynchronizedGeneratorGroovyExtension() {
        super(SYNCHRONIZED_GENERATORS)
    }

    protected SynchronizedGeneratorGroovyExtension(URL config) {
        super(SYNCHRONIZED_GENERATORS, config)
    }

    @Override
    protected SynchronizedGeneratorBuilder createBuilder(Network network, EquipmentConfig equipmentConfig) {
        new SynchronizedGeneratorBuilder(network, equipmentConfig)
    }

    static class SynchronizedGeneratorBuilder extends AbstractGeneratorBuilder {

        EquipmentConfig equipmentConfig

        SynchronizedGeneratorBuilder(Network network, EquipmentConfig equipmentConfig) {
            super(network)
            this.equipmentConfig = equipmentConfig
        }

        @Override
        SynchronizedGenerator build() {
            checkData()
            if (equipmentConfig.isControllable()) {
                new SynchronizedGeneratorControllable(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
            } else {
                new SynchronizedGenerator(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
            }
        }
    }
}
