/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.models.generators.SynchronousGenerator
import com.powsybl.dynawaltz.models.generators.SynchronousGeneratorControllable
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

    protected SynchronousGeneratorGroovyExtension(URL config) {
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
            if (generator && generator.getTerminal().isConnected() &&
                    generator.getTerminal().getBusBreakerView().getBus().getConnectedComponent().getNum() == 0) {
                if (generator.voltageRegulatorOn) {
                    if (equipmentConfig.isControllable()) {
                        new SynchronousGeneratorControllable(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
                    } else {
                        new SynchronousGenerator(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
                    }
                } else {
                    println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + staticId + " voltage regulator is off.")
                }
            } else {
                if (!generator) {
                    println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + staticId + " not present in network.")
                } else if (!generator.getTerminal().isConnected()) {
                    println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + staticId + " not connected.")
                } else if (generator.getTerminal().getBusBreakerView().getBus().getConnectedComponent().getNum() > 0) {
                    println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated because " + staticId + " not in main connected component.")
                }
            }
        }
    }
}
