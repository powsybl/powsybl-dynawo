/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.dsl.models.builders

import com.powsybl.dynamicsimulation.DynamicModel
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
abstract class AbstractEquipmentModelBuilder<T extends Identifiable> implements ModelBuilder<DynamicModel> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(this.class)

    protected Network network
    protected EquipmentConfig equipmentConfig
    protected String dynamicModelId
    protected String staticId
    protected String parameterSetId
    protected T equipment
    private IdentifiableType equipmentType

    AbstractEquipmentModelBuilder(Network network, EquipmentConfig equipmentConfig, IdentifiableType equipmentType) {
        this.network = network
        this.equipmentConfig = equipmentConfig
        this.equipmentType = equipmentType
    }

    void dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId
    }

    void staticId(String staticId) {
        this.staticId = staticId
        equipment = getEquipment()
    }

    void parameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId
    }

    protected boolean checkData() {
        def isInstantiable = true
        if (!staticId) {
            LOGGER.warn("'staticId' field is not set")
            isInstantiable = false
        } else if (!equipment) {
            LOGGER.warn("$equipmentType static id unknown : $staticId")
            isInstantiable = false
        }
        if (!parameterSetId) {
            LOGGER.warn("'parameterSetId' field is not set")
            isInstantiable = false
        }
        if (!dynamicModelId) {
            dynamicModelId = staticId
        }
        isInstantiable
    }

    protected final boolean isInstantiable() {
        checkData().tap {
            if (!it) {
                LOGGER.warn("${equipmentConfig.lib} cannot be instantiated")
            }
        }
    }

    abstract protected T getEquipment();

    @Override
    abstract DynamicModel build();
}
