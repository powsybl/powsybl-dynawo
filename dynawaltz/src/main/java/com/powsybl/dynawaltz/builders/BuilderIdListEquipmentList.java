/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.iidm.network.Identifiable;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BuilderIdListEquipmentList<T extends Identifiable<?>> extends BuilderEquipmentsList<T> {

    public BuilderIdListEquipmentList(String equipmentType, String fieldName) {
        super(equipmentType, fieldName);
    }

    public void addEquipments(Collection<String>[] staticIdsArray, Function<String, T> equipmentSupplier) {
        for (Collection<String> staticIds : staticIdsArray) {
            addEquipment(staticIds, equipmentSupplier);
        }
    }

    private void addEquipment(Collection<String> staticIds, Function<String, T> equipmentSupplier) {
        staticIds.stream()
                .map(equipmentSupplier)
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresentOrElse(equipments::add,
                    () -> missingEquipmentIds.add(staticIds.toString()));
    }
}
