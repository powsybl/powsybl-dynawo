/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.models.generators.EnumGeneratorComponent
import com.powsybl.dynawaltz.models.generators.SynchronousGenerator
import com.powsybl.dynawaltz.models.generators.SynchronousGeneratorControllable
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
@AutoService(DynamicModelGroovyExtension.class)
class SynchronousGeneratorGroovyExtension extends AbstractEquipmentGroovyExtension {

    protected static final String SYNCHRONOUS_GENERATORS = "synchronousGenerators"

    SynchronousGeneratorGroovyExtension() {
        super(SYNCHRONOUS_GENERATORS)
    }

    protected SynchronousGeneratorGroovyExtension(URL config) {
        super(SYNCHRONOUS_GENERATORS, config)
    }

    @Override
    protected SynchronousGeneratorBuilder createBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        new SynchronousGeneratorBuilder(network, equipmentConfig, reporter)
    }

    static class SynchronousGeneratorBuilder extends AbstractGeneratorBuilder {

        SynchronousGeneratorBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
            super(network, equipmentConfig, reporter)
        }

        protected EnumGeneratorComponent getGeneratorComponent() {
            def aux = equipmentConfig.hasAuxiliary()
            def transfo = equipmentConfig.hasTransformer()
            if (aux && transfo) {
                return EnumGeneratorComponent.AUXILIARY_TRANSFORMER
            } else if (transfo) {
                return EnumGeneratorComponent.TRANSFORMER
            } else if (aux) {
                throw new DslException("Generator component auxiliary without transformer is not supported")
            }
            EnumGeneratorComponent.NONE
        }

        @Override
        SynchronousGenerator build() {
            if (isInstantiable()) {
                if (equipmentConfig.isControllable()) {
                    new SynchronousGeneratorControllable(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib, getGeneratorComponent())
                } else {
                    new SynchronousGenerator(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib, getGeneratorComponent())
                }
            } else {
                null
            }
        }
    }
}
