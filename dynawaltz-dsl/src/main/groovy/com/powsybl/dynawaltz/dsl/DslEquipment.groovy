/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl

import com.powsybl.iidm.network.Identifiable
import com.powsybl.iidm.network.IdentifiableType
import org.slf4j.Logger

import java.util.function.Function

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class DslEquipment<T extends Identifiable> {

    String staticId
    T equipment
    final String equipmentType
    final String fieldName

    DslEquipment(String equipmentType, String fieldName) {
        this.equipmentType = equipmentType
        this.fieldName = fieldName
    }

    DslEquipment(IdentifiableType identifiableType, String fieldName) {
        this.equipmentType = identifiableType.toString()
        this.fieldName = fieldName
    }

    DslEquipment(IdentifiableType identifiableType) {
        this(identifiableType, "staticId")
    }

    DslEquipment(String equipmentType) {
        this(equipmentType, "staticId")
    }

    void addEquipment(String equipmentId, Function<String, T> equipmentSupplier) {
        staticId = equipmentId
        equipment = equipmentSupplier(staticId)
    }

    boolean checkEquipmentData(Logger logger) {
        if (!staticId) {
            logger.warn("'${fieldName}' field is not set")
            return false
        } else if (!equipment) {
            logger.warn("${equipmentType} static id unknown : ${staticId}")
            return false
        }
        true
    }
}
