/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.IdentifiableType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BuilderEquipmentsList<T extends Identifiable<?>> {

    private final String equipmentType;
    private final String fieldName;
    // when set to true equipment ids not found in the network are seen as dynamic ids for automatons and reported as such
    private final boolean missingIdsHasDynamicIds;
    protected List<String> missingEquipmentIds = new ArrayList<>();
    protected final List<T> equipments = new ArrayList<>();

    public BuilderEquipmentsList(IdentifiableType identifiableType, String fieldName) {
        this(identifiableType.toString(), fieldName, false);
    }

    public BuilderEquipmentsList(String equipmentType, String fieldName) {
        this(equipmentType, fieldName, false);
    }

    public BuilderEquipmentsList(String equipmentType, String fieldName, boolean missingIdsHasDynamicIds) {
        this.equipmentType = equipmentType;
        this.fieldName = fieldName;
        this.missingIdsHasDynamicIds = missingIdsHasDynamicIds;
    }

    public void addEquipments(String[] staticIds, Function<String, T> equipmentsSupplier) {
        addEquipments(() -> Arrays.stream(staticIds).iterator(), equipmentsSupplier);
    }

    public void addEquipments(Iterable<String> staticIds, Function<String, T> equipmentsSupplier) {
        staticIds.forEach(id -> {
            T equipment = equipmentsSupplier.apply(id);
            if (equipment != null) {
                equipments.add(equipment);
            } else {
                missingEquipmentIds.add(id);
            }
        });
    }

    public boolean checkEquipmentData(Reporter reporter) {
        boolean emptyList = equipments.isEmpty();
        if (missingEquipmentIds.isEmpty() && emptyList) {
            Reporters.reportFieldNotSet(reporter, fieldName);
            return false;
        } else if (!missingIdsHasDynamicIds) {
            missingEquipmentIds.forEach(missingId ->
                    Reporters.reportStaticIdUnknown(reporter, fieldName, missingId, equipmentType));
            if (emptyList) {
                Reporters.reportEmptyList(reporter, fieldName);
            }
            return !emptyList;
        } else {
            missingEquipmentIds.forEach(missingId ->
                    Reporters.reportUnknownStaticIdHandling(reporter, fieldName, missingId, equipmentType));
            return true;
        }
    }

    public List<T> getEquipments() {
        return equipments;
    }

    public List<String> getMissingEquipmentIds() {
        return missingEquipmentIds;
    }
}
