/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.hvdc

import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.Reporters
import com.powsybl.dynawaltz.dsl.builders.AbstractEquipmentModelBuilder
import com.powsybl.dynawaltz.models.Side
import com.powsybl.dynawaltz.models.utils.SideConverter
import com.powsybl.iidm.network.HvdcLine
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.TwoSides

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractHvdcBuilder extends AbstractEquipmentModelBuilder<HvdcLine> {

    protected Side danglingSide

    AbstractHvdcBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, IdentifiableType.HVDC_LINE, reporter)
    }

    void dangling(TwoSides danglingSide) {
        this.danglingSide = SideConverter.convert(danglingSide)
    }

    @Override
    protected void checkData() {
        super.checkData()
        def isDangling = equipmentConfig.isDangling()
        if (isDangling && !danglingSide) {
            Reporters.reportFieldNotSet(reporter, "dangling")
            isInstantiable = false
        } else if (!isDangling && danglingSide) {
            Reporters.reportFieldSetWithWrongEquipment(reporter, "dangling",  equipmentConfig.getLib())
            isInstantiable = false
        }
    }

    @Override
    protected HvdcLine findEquipment(String staticId) {
        network.getHvdcLine(staticId)
    }
}
