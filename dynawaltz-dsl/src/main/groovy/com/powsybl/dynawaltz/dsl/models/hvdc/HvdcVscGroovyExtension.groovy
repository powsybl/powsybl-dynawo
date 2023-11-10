/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.hvdc

import com.google.auto.service.AutoService
import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractEquipmentGroovyExtension
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.models.hvdc.HvdcVsc
import com.powsybl.dynawaltz.models.hvdc.HvdcVscDangling
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(DynamicModelGroovyExtension.class)
class HvdcVscGroovyExtension extends AbstractEquipmentGroovyExtension {

    protected static final String HVDC_VSC = "hvdcVsc"

    HvdcVscGroovyExtension() {
        super(HVDC_VSC)
    }

    @Override
    protected HvdcBuilder createBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        new HvdcBuilder(network, equipmentConfig, reporter)
    }

    static class HvdcBuilder extends AbstractHvdcBuilder {

        HvdcBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
            super(network, equipmentConfig, reporter)
        }

        @Override
        HvdcVsc build() {
            if (isInstantiable()) {
                if (equipmentConfig.isDangling()) {
                    new HvdcVscDangling(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib, danglingSide)
                } else {
                    new HvdcVsc(dynamicModelId, equipment, parameterSetId, equipmentConfig.lib)
                }
            } else {
                null
            }
        }
    }
}
