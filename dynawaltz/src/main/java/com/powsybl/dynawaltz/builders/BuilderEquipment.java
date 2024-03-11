/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.IdentifiableType;

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

    protected boolean fromStaticId;
    protected String staticId;
    protected T equipment;
    private final String equipmentType;
    private final String fieldName;

    public BuilderEquipment(String equipmentType, String fieldName) {
        this.equipmentType = equipmentType;
        this.fieldName = fieldName;
    }

    public BuilderEquipment(IdentifiableType identifiableType, String fieldName) {
        this.equipmentType = identifiableType.toString();
        this.fieldName = fieldName;
    }

    public BuilderEquipment(IdentifiableType identifiableType) {
        this(identifiableType, DEFAULT_FIELD_NAME);
    }

    public BuilderEquipment(String equipmentType) {
        this(equipmentType, DEFAULT_FIELD_NAME);
    }

    public void addEquipment(String equipmentId, Function<String, T> equipmentSupplier) {
        fromStaticId = true;
        staticId = equipmentId;
        equipment = equipmentSupplier.apply(staticId);
    }

    public void addEquipment(T equipment, Predicate<T> equipmentChecker) {
        fromStaticId = false;
        staticId = equipment.getId();
        if (equipmentChecker.test(equipment)) {
            this.equipment = equipment;
        }
    }

    public boolean checkEquipmentData(Reporter reporter) {
        if (!hasStaticId()) {
            Reporters.reportFieldNotSet(reporter, fieldName);
            return false;
        } else if (equipment == null) {
            if (fromStaticId) {
                Reporters.reportStaticIdUnknown(reporter, fieldName, staticId, equipmentType);
            } else {
                Reporters.reportDifferentNetwork(reporter, EQUIPMENT_FIELD_NAME, staticId, equipmentType);
            }
            return false;
        }
        return true;
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
