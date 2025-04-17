/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.iidm.network.Identifiable;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents an equipment field identified by a static ID in a builder
 * Verifies if the corresponding equipment with the specified type exists, log the error otherwise
 *
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BuilderEquipment<T extends Identifiable<?>> {

    private static final String DEFAULT_FIELD_NAME = "staticId";

    private static final String EQUIPMENT_FIELD_NAME = "equipment";

    protected String staticId;
    protected T equipment;
    protected final String equipmentType;
    protected final String fieldName;
    protected final ReportNode reportNode;

    public BuilderEquipment(String equipmentType, String fieldName, ReportNode reportNode) {
        this.equipmentType = equipmentType;
        this.fieldName = fieldName;
        this.reportNode = reportNode;
    }

    public BuilderEquipment(String equipmentType, ReportNode reportNode) {
        this(equipmentType, DEFAULT_FIELD_NAME, reportNode);
    }

    public void addEquipment(String equipmentId, Function<String, T> equipmentSupplier) {
        staticId = equipmentId;
        equipment = equipmentSupplier.apply(staticId);
        if (equipment == null) {
            BuilderReports.reportStaticIdUnknown(reportNode, fieldName, staticId, equipmentType);
        }
    }

    public void addEquipment(String equipmentId, Function<String, T> equipmentSupplier,
                             EquipmentPredicate<T> equipmentPredicate) {
        staticId = equipmentId;
        T equipment = equipmentSupplier.apply(staticId);
        if (equipment == null) {
            BuilderReports.reportStaticIdUnknown(reportNode, fieldName, staticId, equipmentType);
        } else if (equipmentPredicate.test(equipment, fieldName, reportNode)) {
            this.equipment = equipment;
        }
    }

    public void addEquipment(T equipment, Predicate<T> equipmentChecker) {
        staticId = equipment.getId();
        if (equipmentChecker.test(equipment)) {
            this.equipment = equipment;
        } else {
            BuilderReports.reportDifferentNetwork(reportNode, EQUIPMENT_FIELD_NAME, staticId, equipmentType);
        }
    }

    public void addEquipment(T equipment, Predicate<T> equipmentChecker, EquipmentPredicate<T> equipmentPredicate) {
        staticId = equipment.getId();
        if (!equipmentChecker.test(equipment)) {
            BuilderReports.reportDifferentNetwork(reportNode, EQUIPMENT_FIELD_NAME, staticId, equipmentType);
        } else if (equipmentPredicate.test(equipment, fieldName, reportNode)) {
            this.equipment = equipment;
        }
    }

    public boolean checkEquipmentData() {
        if (staticId == null) {
            BuilderReports.reportFieldNotSet(reportNode, fieldName);
            return false;
        }
        return equipment != null;
    }

    public String getStaticId() {
        return staticId;
    }

    public T getEquipment() {
        return equipment;
    }

    public boolean hasEquipment() {
        return equipment != null;
    }

    public String getFieldName() {
        return fieldName;
    }
}
