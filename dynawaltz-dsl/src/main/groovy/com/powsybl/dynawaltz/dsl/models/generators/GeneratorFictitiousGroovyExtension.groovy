/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractSimpleEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.models.generators.GeneratorFictitious
import com.powsybl.iidm.network.Network

/**
 * @author Dimitri Baudrier {@literal <dimitri.baudrier at rte-france.com>}
 */
@AutoService(DynamicModelGroovyExtension.class)
class GeneratorFictitiousGroovyExtension extends AbstractSimpleEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    GeneratorFictitiousGroovyExtension() {
        super("GeneratorFictitious")
    }

    @Override
    protected GeneratorFictitiousBuilder createBuilder(Network network, EquipmentConfig equipmentConfig) {
        new GeneratorFictitiousBuilder(network, equipmentConfig)
    }

    static class GeneratorFictitiousBuilder extends AbstractGeneratorBuilder {

        GeneratorFictitiousBuilder(Network network, EquipmentConfig equipmentConfig) {
            super(network, equipmentConfig)
        }

        @Override
        GeneratorFictitious build() {
            isInstantiable() ? new GeneratorFictitious(dynamicModelId, equipment, parameterSetId) : null
        }
    }
}
