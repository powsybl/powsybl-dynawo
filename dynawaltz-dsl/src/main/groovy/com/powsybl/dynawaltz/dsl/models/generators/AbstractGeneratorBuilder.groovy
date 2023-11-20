/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.builders.AbstractEquipmentModelBuilder
import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
abstract class AbstractGeneratorBuilder extends AbstractEquipmentModelBuilder<Generator> {

    AbstractGeneratorBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig, IdentifiableType.GENERATOR)
    }

    @Override
    protected Generator findEquipment(String staticId) {
        network.getGenerator(staticId)
    }
}
