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
    protected final ReportNode reportNode;

    protected List<String> missingEquipmentIds = new ArrayList<>();
    protected List<T> equipments = new ArrayList<>();

    public BuilderEquipmentsList(String equipmentType, String fieldName, ReportNode reportNode) {
        this.equipmentType = equipmentType;
        this.fieldName = fieldName;
        this.reportNode = reportNode;
    }

    public void addEquipments(String[] staticIds, Function<String, T> equipmentsSupplier) {
        addEquipments(() -> Arrays.stream(staticIds).iterator(), equipmentsSupplier);
    }

    public void addEquipments(Iterable<String> staticIds, Function<String, T> equipmentsSupplier) {
        staticIds.forEach(id -> addEquipment(id, equipmentsSupplier));
        reportIfEmptyList();
    }

    public void addEquipments(String[] staticIds, Function<String, T> equipmentsSupplier,
                              EquipmentChecker<T> equipmentChecker) {
        addEquipments(() -> Arrays.stream(staticIds).iterator(), equipmentsSupplier, equipmentChecker);
    }

    public void addEquipments(Iterable<String> staticIds, Function<String, T> equipmentsSupplier,
                              EquipmentChecker<T> equipmentChecker) {
        staticIds.forEach(id -> addEquipment(id, equipmentsSupplier, equipmentChecker));
        reportIfEmptyList();
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
                             EquipmentChecker<T> equipmentChecker) {
        T equipment = equipmentsSupplier.apply(staticId);
        if (equipment == null) {
            handleMissingId(staticId);
        } else if (equipmentChecker.test(equipment, fieldName, reportNode)) {
            equipments.add(equipment);
        } else {
            missingEquipmentIds.add(staticId);
        }
    }

    protected void handleMissingId(String staticId) {
        missingEquipmentIds.add(staticId);
        BuilderReports.reportStaticIdUnknown(reportNode, fieldName, staticId, equipmentType);
    }

    protected void reportIfEmptyList() {
        if (equipments.isEmpty()) {
            BuilderReports.reportEmptyList(reportNode, fieldName);
        }
    }

    public boolean checkEquipmentData() {

        if (equipments.isEmpty() && missingEquipmentIds.isEmpty()) {
            BuilderReports.reportFieldNotSet(reportNode, fieldName);
            return false;
        }

        if (!missingEquipmentIds.isEmpty()) {
            missingEquipmentIds.forEach(missingEquipmentId -> {
                BuilderReports.reportFieldSetWithWrongEquipment(
                        reportNode,
                        fieldName,
                        missingEquipmentId
                );
            });
            return false;
        }

        return true;
    }

    public List<T> getEquipments() {
        return equipments;
    }
}
