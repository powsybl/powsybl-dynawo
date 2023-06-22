/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.builders

import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynawaltz.dsl.DslEquipment
import com.powsybl.dynawaltz.dsl.EquipmentConfig
import com.powsybl.dynawaltz.dsl.ModelBuilder
import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractEquipmentModelBuilder<T extends Identifiable> extends AbstractDynamicModelBuilder {

    protected final EquipmentConfig equipmentConfig
    protected final DslEquipment<T> dslEquipment

    protected AbstractEquipmentModelBuilder(Network network, EquipmentConfig equipmentConfig, IdentifiableType equipmentType) {
        super(network)
        this.equipmentConfig = equipmentConfig
        this.dslEquipment = new DslEquipment<T>(equipmentType)
    }

    void staticId(String staticId) {
        dslEquipment.tap {
            it.staticId = staticId
            equipment = findEquipment(staticId)
        }
    }

    @Override
    protected void checkData() {
        checkEquipmentData(dslEquipment)
        if (!parameterSetId) {
            LOGGER.warn("'parameterSetId' field is not set")
            isInstantiable = false
        }
        if (!dynamicModelId) {
            dynamicModelId = dslEquipment.staticId
        }
    }

    abstract protected T findEquipment(String staticId);

    T getEquipment() {
        dslEquipment.equipment
    }

    String getLib() {
        equipmentConfig.getLib()
    }

    @Override
    abstract DynamicModel build();
}
