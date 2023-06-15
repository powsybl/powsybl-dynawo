/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.hvdc

import com.powsybl.dsl.DslException
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.models.Side
import com.powsybl.dynawaltz.models.utils.SideConverter
import com.powsybl.iidm.network.Branch
import com.powsybl.iidm.network.HvdcLine
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractHvdcBuilder extends AbstractDynamicModelBuilder {

    HvdcLine hvdc
    EquipmentConfig equipmentConfig
    Side danglingSide

    AbstractHvdcBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network)
        this.equipmentConfig = equipmentConfig
    }

    void dangling(Branch.Side danglingSide) {
        if (equipmentConfig.isDangling()) {
            this.danglingSide = SideConverter.convert(danglingSide)
        } else {
            throw new DslException("'dangling' field is set on a non dangling hvdc : ${equipmentConfig.lib}")
        }
    }

    void checkData() {
        super.checkData()
        hvdc = network.getHvdcLine(staticId)
        /*if (hvdc == null) {
            throw new DslException("Hvdc line static id unknown: " + staticId)
        }*/
    }
}
