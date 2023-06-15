/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.hvdc

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.models.hvdc.HvdcPv
import com.powsybl.dynawaltz.models.hvdc.HvdcPvDangling
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class HvdcPvGroovyExtension extends AbstractEquipmentGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    protected static final String HVDC_PV = "hvdcPv"

    HvdcPvGroovyExtension() {
        super(HVDC_PV)
    }

    protected HvdcPvGroovyExtension(URL config) {
        super(HVDC_PV, config)
    }

    @Override
    protected HvdcBuilder createBuilder(Network network, EquipmentConfig equipmentConfig) {
        new HvdcBuilder(network, equipmentConfig)
    }

    static class HvdcBuilder extends AbstractHvdcBuilder {

        HvdcBuilder(Network network, EquipmentConfig equipmentConfig) {
            super(network, equipmentConfig)
        }

        @Override
        HvdcPv build() {
            checkData()
            println dynamicModelId
            println hvdc.getConverterStation1().getTerminal().isConnected()
            println hvdc.getConverterStation2().getTerminal().isConnected()
            if (hvdc && hvdc.getConverterStation1().getTerminal().isConnected() && hvdc.getConverterStation2().getTerminal().isConnected()) {
                if (equipmentConfig.isDangling()) {
                    new HvdcPvDangling(dynamicModelId, hvdc, parameterSetId, equipmentConfig.lib, danglingSide)
                } else {
                    new HvdcPv(dynamicModelId, hvdc, parameterSetId, equipmentConfig.lib)
                }
            } else {
                println(equipmentConfig.lib + " " + dynamicModelId + " not instantiated.")
            }
        }
    }
}
