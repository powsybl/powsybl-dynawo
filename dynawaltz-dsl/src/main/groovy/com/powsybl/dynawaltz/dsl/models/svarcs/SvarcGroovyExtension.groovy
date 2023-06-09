/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.svarcs

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.StaticVarCompensator

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class SvarcGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    protected static final String SVARC = "staticVarCompensators"

    SvarcGroovyExtension() {
        super(SVARC)
    }

    @Override
    protected SvcBuilder createBuilder(Network network, EquipmentConfig equipmentConfig) {
        new SvcBuilder(network, equipmentConfig)
    }

    static class SvcBuilder extends AbstractDynamicModelBuilder {

        StaticVarCompensator svarc
        EquipmentConfig equipmentConfig

        SvcBuilder(Network network, EquipmentConfig equipmentConfig) {
            super(network)
            this.equipmentConfig = equipmentConfig
        }

        void checkData() {
            super.checkData()
            svarc = network.getStaticVarCompensator(staticId)
            if (svarc == null) {
                throw new DslException("Static var compensator static id unknown: " + staticId)
            }
        }

        @Override
        com.powsybl.dynawaltz.models.svarcs.StaticVarCompensator build() {
            checkData()
            new com.powsybl.dynawaltz.models.svarcs.StaticVarCompensator(dynamicModelId, svarc, parameterSetId, equipmentConfig.lib)
        }
    }
}
