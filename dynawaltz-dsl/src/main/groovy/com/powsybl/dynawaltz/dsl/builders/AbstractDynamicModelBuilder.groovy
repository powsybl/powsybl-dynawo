/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.builders

import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynawaltz.dsl.DslEquipment
import com.powsybl.dynawaltz.dsl.ModelBuilder
import com.powsybl.iidm.network.Network
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractDynamicModelBuilder {

    protected static final Logger LOGGER = LoggerFactory.getLogger(this.class)

    protected final Network network
    protected boolean isInstantiable = true

    protected AbstractDynamicModelBuilder(Network network) {
        this.network = network
    }

    abstract protected void checkData()

    protected void checkEquipmentData(DslEquipment dslEquipment) {
        if (!dslEquipment.staticId) {
            LOGGER.warn("'${dslEquipment.fieldName}' field is not set")
            isInstantiable = false
        } else if (!dslEquipment.equipment) {
            LOGGER.warn("${dslEquipment.equipmentType} static id unknown : ${dslEquipment.staticId}")
            isInstantiable = false
        }
    }

    protected final boolean isInstantiable() {
        checkData()
        if (!isInstantiable) {
            LOGGER.warn("${getLib()} cannot be instantiated")
        }
        isInstantiable
    }

    abstract protected String getLib()
}
