/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.hvdc

import com.google.auto.service.AutoService
import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.models.builders.AbstractConfigDynamicModelBuilder
import com.powsybl.dynawaltz.models.hvdc.HvdcModel
import com.powsybl.iidm.network.HvdcLine
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class HvdcGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    protected static final String HVDC = "hvdc"

    HvdcGroovyExtension() {
        super(HVDC)
    }

    @Override
    protected HvdcBuilder createBuilder(Network network, EquipmentConfig equipmentConfig) {
        new HvdcBuilder(network, equipmentConfig)
    }

    static class HvdcBuilder extends AbstractConfigDynamicModelBuilder {

        HvdcLine hvdc

        HvdcBuilder(Network network, EquipmentConfig equipmentConfig) {
            super(network, equipmentConfig)
        }

        void checkData() {
            super.checkData()
            hvdc = network.getHvdcLine(staticId)
            if (hvdc == null) {
                throw new DslException("Hvdc line static id unknown: " + staticId)
            }
        }

        @Override
        HvdcModel build() {
            checkData()
            new HvdcModel(dynamicModelId, hvdc, parameterSetId, equipmentConfig.lib)
        }
    }
}
