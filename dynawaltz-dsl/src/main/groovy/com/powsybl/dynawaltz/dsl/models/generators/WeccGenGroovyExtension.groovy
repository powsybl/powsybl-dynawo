/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
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
import com.powsybl.dynawaltz.models.generators.SynchronizedWeccGen
import com.powsybl.dynawaltz.models.generators.WeccGen
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class WeccGenGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    private static final String WECC = "wecc"

    WeccGenGroovyExtension() {
        super(WECC)
    }

    @Override
    protected WeccGenBuilder createBuilder(Network network, EquipmentConfig equipmentConfig) {
        new WeccGenBuilder(network, equipmentConfig)
    }

    static class WeccGenBuilder extends AbstractGeneratorBuilder {

        WeccGenBuilder(Network network, EquipmentConfig equipmentConfig) {
            super(network, equipmentConfig)
        }

        @Override
        WeccGen build() {
            if (isInstantiable()) {
                if (equipmentConfig.isSynchronized()) {
                    new SynchronizedWeccGen(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib, equipmentConfig.prefix)
                } else {
                    new WeccGen(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib, equipmentConfig.prefix)
                }
            } else {
                null
            }
        }
    }
}
