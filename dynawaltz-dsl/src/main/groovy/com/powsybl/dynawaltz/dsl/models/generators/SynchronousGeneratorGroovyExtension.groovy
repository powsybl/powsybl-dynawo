/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.models.generators.SynchronousGenerator
import com.powsybl.dynawaltz.models.generators.SynchronousGeneratorControllable
import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicModelGroovyExtension.class)
class SynchronousGeneratorGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    protected static final String SYNCHRONOUS_GENERATORS = "synchronousGenerators"

    SynchronousGeneratorGroovyExtension() {
        super(SYNCHRONOUS_GENERATORS)
    }

    protected GeneratorSynchronousGroovyExtension(URL config) {
        super(SYNCHRONOUS_GENERATORS, config)
    }

    @Override
    protected SynchronousGeneratorBuilder createBuilder(Network network, EquipmentConfig equipmentConfig) {
        new SynchronousGeneratorBuilder(network, equipmentConfig)
    }

    static class SynchronousGeneratorBuilder extends AbstractGeneratorBuilder {

        EquipmentConfig equipmentConfig

        SynchronousGeneratorBuilder(Network network, EquipmentConfig equipmentConfig) {
            super(network)
            this.equipmentConfig = equipmentConfig
        }

        @Override
        SynchronousGenerator build() {
            checkData()
            if (equipmentConfig.isControllable()) {
                new SynchronousGeneratorControllable(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
            } else {
                new SynchronousGenerator(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
            }
        }
    }
}
