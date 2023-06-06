/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.wecc

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.models.wecc.SynchronizedWecc
import com.powsybl.dynawaltz.models.wecc.Wecc
import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class WtWeccGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    private static final String WECC = "wecc"

    WtWeccGroovyExtension() {
        super(WECC)
    }

    @Override
    protected WtWeccBuilder createBuilder(Network network, EquipmentConfig equipmentConfig) {
        new WtWeccBuilder(network, equipmentConfig)
    }

    static class WtWeccBuilder extends AbstractDynamicModelBuilder {

        Generator generator
        EquipmentConfig equipmentConfig

        WtWeccBuilder(Network network, EquipmentConfig equipmentConfig) {
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
        Wecc build() {
            checkData()
            if (equipmentConfig.isSynchronized()) {
                new SynchronizedWecc(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
            } else {
                new Wecc(dynamicModelId, generator, parameterSetId, equipmentConfig.lib)
            }
        }
    }
}
