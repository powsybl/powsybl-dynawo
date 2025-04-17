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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BuilderEquipmentsList<T extends Identifiable<?>> {

    protected final String equipmentType;
    protected final String fieldName;
    // when set to true equipment ids not found in the network are seen as dynamic ids for automatons and reported as such
    protected final boolean missingIdsAsDynamicIds;
    protected final ReportNode reportNode;

    protected List<String> missingEquipmentIds = new ArrayList<>();
    protected List<T> equipments = new ArrayList<>();
    protected boolean failedPredicate = false;

    public BuilderEquipmentsList(String equipmentType, String fieldName, ReportNode reportNode) {
        this(equipmentType, fieldName, false, reportNode);
    }

    public BuilderEquipmentsList(String equipmentType, String fieldName, boolean missingIdsAsDynamicIds, ReportNode reportNode) {
        this.equipmentType = equipmentType;
        this.fieldName = fieldName;
        this.missingIdsAsDynamicIds = missingIdsAsDynamicIds;
        this.reportNode = reportNode;
    }

    public void addEquipments(String[] staticIds, Function<String, T> equipmentsSupplier) {
        addEquipments(() -> Arrays.stream(staticIds).iterator(), equipmentsSupplier);
    }

    public void addEquipments(Iterable<String> staticIds, Function<String, T> equipmentsSupplier) {
        staticIds.forEach(id -> addEquipment(id, equipmentsSupplier));
    }

    public void addEquipments(String[] staticIds, Function<String, T> equipmentsSupplier,
                              EquipmentPredicate<T> equipmentPredicate) {
        addEquipments(() -> Arrays.stream(staticIds).iterator(), equipmentsSupplier, equipmentPredicate);
    }

    public void addEquipments(Iterable<String> staticIds, Function<String, T> equipmentsSupplier,
                              EquipmentPredicate<T> equipmentPredicate) {
        staticIds.forEach(id -> addEquipment(id, equipmentsSupplier, equipmentPredicate));
    }

    public void addEquipment(String staticId, Function<String, T> equipmentsSupplier) {
        T equipment = equipmentsSupplier.apply(staticId);
        if (equipment == null) {
            handleMissingId(staticId);
        } else {
            equipments.add(equipment);
        }
    }

    public void addEquipment(String staticId, Function<String, T> equipmentsSupplier,
                             EquipmentPredicate<T> equipmentPredicate) {
        T equipment = equipmentsSupplier.apply(staticId);
        if (equipment == null) {
            handleMissingId(staticId);
        } else if (equipmentPredicate.test(equipment, fieldName, reportNode)) {
            equipments.add(equipment);
        } else {
            failedPredicate = true;
        }
    }

    private void handleMissingId(String staticId) {
        missingEquipmentIds.add(staticId);
        if (missingIdsAsDynamicIds) {
            BuilderReports.reportUnknownStaticIdHandling(reportNode, fieldName, staticId, equipmentType);
        } else {
            BuilderReports.reportStaticIdUnknown(reportNode, fieldName, staticId, equipmentType);
        }
    }

    public boolean checkEquipmentData(ReportNode reportNode) {
        boolean emptyList = equipments.isEmpty();
        if (missingEquipmentIds.isEmpty() && emptyList && !failedPredicate) {
            BuilderReports.reportFieldNotSet(reportNode, fieldName);
            return false;
        } else if (!missingIdsAsDynamicIds && emptyList) {
            BuilderReports.reportEmptyList(reportNode, fieldName);
            return false;
        }
        return true;
    }

    public List<T> getEquipments() {
        return equipments;
    }

    public List<String> getMissingEquipmentIds() {
        return missingEquipmentIds;
    }
}
