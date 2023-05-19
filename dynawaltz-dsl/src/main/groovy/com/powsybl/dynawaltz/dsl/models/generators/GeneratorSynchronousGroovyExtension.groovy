/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronous
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronousControllable
import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorSynchronousGroovyExtension extends AbstractEquipmentGroovyExtension<GeneratorSynchronous> {

    protected static final String SYNCHRONOUS_GENERATORS = "synchronousGenerators"

    GeneratorSynchronousGroovyExtension() {
        super(SYNCHRONOUS_GENERATORS)
    }

    @Override
    protected GeneratorSynchronousBuilder createBuilder(Network network, EquipmentConfig equipmentConfig) {
        new GeneratorSynchronousBuilder(network, equipmentConfig)
    }

    static class GeneratorSynchronousBuilder extends AbstractDynamicModelBuilder {

        Generator generator
        EquipmentConfig equipmentConfig

        GeneratorSynchronousBuilder(Network network, EquipmentConfig equipmentConfig) {
            super(network)
            this.equipmentConfig = equipmentConfig
        }

        void checkData() {
            super.checkData()
            generator = network.getGenerator(staticId)
            if (generator == null) {
                throw new DslException("Generator static id unknown: " + staticId)
            }
        }

        @Override
        GeneratorSynchronous build() {
            checkData()
            if (equipmentConfig.isControllable()) {
                new GeneratorSynchronousControllable(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
            } else {
                new GeneratorSynchronous(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
            }
        }
    }
}
