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

import java.util.function.Function
import java.util.function.Predicate

/**
 * Same as DslEquipment but the equipment type could be different subtypes of the specified T type
 * (for example a load OR a generator with T as an Injection)
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DslFilteredEquipment<T extends Identifiable> extends DslEquipment<T> {

    protected final Predicate<IdentifiableType> typePredicate

    DslFilteredEquipment(String equipmentType, Predicate<IdentifiableType> typePredicate) {
        super(equipmentType)
        this.typePredicate = typePredicate
    }

    DslFilteredEquipment(String equipmentType, String fieldName, Predicate<IdentifiableType> typePredicate) {
        super(equipmentType, fieldName)
        this.typePredicate = typePredicate
    }

    @Override
    void addEquipment(String equipmentId, Function<String, T> equipmentSupplier) {
        staticId = equipmentId
        T equipment = equipmentSupplier(staticId)
        if(equipment && typePredicate.test(equipment.type)) {
            this.equipment = equipment
        }
    }
}