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
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents an equipment field identified by a static ID in a builder
 * Verifies if the corresponding equipment with the specified type exists, log the error otherwise
 *
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BuilderEquipment<T extends Identifiable<?>> {

    @FunctionalInterface
    public interface EquipmentPredicate<T> {
        boolean test(T equipment, String fieldName, ReportNode reportNode);
    }

    private static final String DEFAULT_FIELD_NAME = "staticId";
    private static final String EQUIPMENT_FIELD_NAME = "equipment";

    protected boolean fromStaticId;
    protected String staticId;
    protected T equipment;
    private final String equipmentType;
    private final String fieldName;
    private final EquipmentPredicate<T> equipmentPredicate;

    public BuilderEquipment(String equipmentType, String fieldName, EquipmentPredicate<T> equipmentPredicate) {
        this.equipmentType = equipmentType;
        this.fieldName = fieldName;
        this.equipmentPredicate = equipmentPredicate;
    }

    public BuilderEquipment(String equipmentType, EquipmentPredicate<T> equipmentPredicate) {
        this(equipmentType, DEFAULT_FIELD_NAME, equipmentPredicate);
    }

    public BuilderEquipment(String equipmentType, String fieldName) {
        this(equipmentType, fieldName, (eq, f, r) -> true);
    }

    public BuilderEquipment(String equipmentType) {
        this(equipmentType, DEFAULT_FIELD_NAME);
    }

    public BuilderEquipment(IdentifiableType identifiableType) {
        this(identifiableType.toString());
    }

    public BuilderEquipment(IdentifiableType identifiableType, String fieldName) {
        this(identifiableType.toString(), fieldName);
    }

    public void addEquipment(String equipmentId, Function<String, T> equipmentSupplier) {
        fromStaticId = true;
        staticId = equipmentId;
        equipment = equipmentSupplier.apply(staticId);
    }

    public void addEquipment(T equipment, Network network) {
        fromStaticId = false;
        staticId = equipment.getId();
        this.equipment = Objects.equals(network, equipment.getNetwork()) ? equipment : null;
    }

    public boolean checkEquipmentData(ReportNode reportNode) {
        if (!hasStaticId()) {
            BuilderReports.reportFieldNotSet(reportNode, fieldName);
            return false;
        } else if (equipment == null) {
            if (fromStaticId) {
                BuilderReports.reportStaticIdUnknown(reportNode, fieldName, staticId, equipmentType);
            } else {
                BuilderReports.reportDifferentNetwork(reportNode, EQUIPMENT_FIELD_NAME, staticId, equipmentType);
            }
            return false;
        }
        return equipmentPredicate.test(equipment, fieldName, reportNode);
    }

    public String getStaticId() {
        return staticId;
    }

    public boolean hasStaticId() {
        return staticId != null;
    }

    public T getEquipment() {
        return equipment;
    }

    public boolean hasEquipment() {
        return equipment != null;
    }
}
