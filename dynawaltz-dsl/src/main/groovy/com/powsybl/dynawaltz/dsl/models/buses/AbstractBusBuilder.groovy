/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.buses

import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.builders.AbstractEquipmentModelBuilder
import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractBusBuilder extends AbstractEquipmentModelBuilder<Bus> {

    AbstractBusBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, IdentifiableType.BUS, reporter)
    }

    @Override
    protected Bus findEquipment(String staticId) {
       network.getBusBreakerView().getBus(staticId)
    }
}