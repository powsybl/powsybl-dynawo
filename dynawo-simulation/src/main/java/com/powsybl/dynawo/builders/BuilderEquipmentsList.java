/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.ReportNode;
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
    protected List<T> equipments = new ArrayList<>();
    private final BuilderEquipment.EquipmentPredicate<T> equipmentPredicate;

    public BuilderEquipmentsList(IdentifiableType identifiableType, String fieldName) {
        this(identifiableType.toString(), fieldName, false);
    }

    public BuilderEquipmentsList(String equipmentType, String fieldName) {
        this(equipmentType, fieldName, false);
    }

    public BuilderEquipmentsList(String equipmentType, String fieldName, boolean missingIdsHasDynamicIds) {
        this(equipmentType, fieldName, missingIdsHasDynamicIds, null);
    }

    public BuilderEquipmentsList(String equipmentType, String fieldName, boolean missingIdsHasDynamicIds,
                                 BuilderEquipment.EquipmentPredicate<T> equipmentPredicate) {
        this.equipmentType = equipmentType;
        this.fieldName = fieldName;
        this.missingIdsHasDynamicIds = missingIdsHasDynamicIds;
        this.equipmentPredicate = equipmentPredicate;
    }

    public void addEquipments(String[] staticIds, Function<String, T> equipmentsSupplier) {
        addEquipments(() -> Arrays.stream(staticIds).iterator(), equipmentsSupplier);
    }

    public void addEquipments(Iterable<String> staticIds, Function<String, T> equipmentsSupplier) {
        staticIds.forEach(id -> addEquipment(id, equipmentsSupplier));
    }

    public void addEquipment(String staticId, Function<String, T> equipmentsSupplier) {
        T equipment = equipmentsSupplier.apply(staticId);
        if (equipment != null) {
            equipments.add(equipment);
        } else {
            missingEquipmentIds.add(staticId);
        }
    }

    public boolean checkEquipmentData(ReportNode reportNode) {
        boolean emptyList = equipments.isEmpty();
        if (missingEquipmentIds.isEmpty() && emptyList) {
            BuilderReports.reportFieldNotSet(reportNode, fieldName);
            return false;
        } else if (!missingIdsHasDynamicIds) {
            missingEquipmentIds.forEach(missingId ->
                    BuilderReports.reportStaticIdUnknown(reportNode, fieldName, missingId, equipmentType));
            if (emptyList) {
                BuilderReports.reportEmptyList(reportNode, fieldName);
            } else if (equipmentPredicate != null) {
                equipments = equipments.stream().filter(eq -> equipmentPredicate.test(eq, fieldName, reportNode)).toList();
            }
            return !equipments.isEmpty();
        } else {
            missingEquipmentIds.forEach(missingId ->
                    BuilderReports.reportUnknownStaticIdHandling(reportNode, fieldName, missingId, equipmentType));
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
