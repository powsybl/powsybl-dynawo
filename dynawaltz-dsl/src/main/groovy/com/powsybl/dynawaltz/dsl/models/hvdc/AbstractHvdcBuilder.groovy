/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.hvdc

import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.builders.AbstractEquipmentModelBuilder
import com.powsybl.dynawaltz.models.Side
import com.powsybl.dynawaltz.models.utils.SideConverter
import com.powsybl.iidm.network.Branch
import com.powsybl.iidm.network.HvdcLine
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractHvdcBuilder extends AbstractEquipmentModelBuilder<HvdcLine> {

    protected Side danglingSide

    AbstractHvdcBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig, IdentifiableType.HVDC_LINE)
    }

    void dangling(Branch.Side danglingSide) {
        this.danglingSide = SideConverter.convert(danglingSide)
    }

    @Override
    protected void checkData() {
        super.checkData()
        def isDangling = equipmentConfig.isDangling()
        if (isDangling && !danglingSide) {
            LOGGER.warn("${getLib()}: 'dangling' field is not set")
            isInstantiable = false
        } else if (!isDangling && danglingSide) {
            LOGGER.warn("${getLib()}: 'dangling' field is set on a non dangling hvdc")
            isInstantiable = false
        }
    }

    @Override
    protected HvdcLine findEquipment(String staticId) {
        network.getHvdcLine(staticId)
    }
}
