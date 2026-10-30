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
import com.powsybl.iidm.network.VoltageLevel;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Represents an equipment list field identified by a list of static ID in a builder
 * Verifies if the corresponding equipments with the specified type exist, log the missing equipments
 *
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

    public void addVoltageLevelEquipments(String voltageLevelId, Function<String, VoltageLevel> voltageLevelsSupplier,
                                          Function<VoltageLevel, Stream<T>> equipmentsSupplier) {
        addVoltageLevelEquipments(List.of(voltageLevelId), voltageLevelsSupplier, equipmentsSupplier);
    }

    public void addVoltageLevelEquipments(String[] voltageLevelIds, Function<String, VoltageLevel> voltageLevelsSupplier,
                                          Function<VoltageLevel, Stream<T>> equipmentsSupplier) {
        addVoltageLevelEquipments(Arrays.asList(voltageLevelIds), voltageLevelsSupplier, equipmentsSupplier);
    }

    public void addVoltageLevelEquipments(Collection<String> voltageLevelIds, Function<String, VoltageLevel> voltageLevelsSupplier,
                                          Function<VoltageLevel, Stream<T>> equipmentsSupplier) {
        voltageLevelIds.stream()
                .map(vlId -> {
                    VoltageLevel vl = voltageLevelsSupplier.apply(vlId);
                    if (vl == null) {
                        missingEquipmentIds.add(vlId);
                        BuilderReports.reportStaticIdUnknown(reportNode, fieldName + "VoltageLevels", vlId,
                                IdentifiableType.VOLTAGE_LEVEL.toString());
                    }
                    return vl;
                })
                .filter(Objects::nonNull)
                .flatMap(equipmentsSupplier)
                .forEach(eq -> equipments.add(eq));
        if (equipments.isEmpty()) {
            BuilderReports.reportEmptyList(reportNode, fieldName);
            if (missingEquipmentIds.isEmpty()) {
                missingEquipmentIds.addAll(voltageLevelIds);
            }
        }
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
        boolean emptyList = equipments.isEmpty();
        if (emptyList && missingEquipmentIds.isEmpty()) {
            BuilderReports.reportFieldNotSet(reportNode, fieldName);
            return false;
        }
        return !emptyList;
    }

    public List<T> getEquipments() {
        return equipments;
    }
}
