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

/**
 * Represents an equipment field identified by a static ID in the groovy script
 * Verifies if the corresponding equipment with the specified type exists, log the error otherwise
 *
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DslEquipment<T extends Identifiable<?>> {
//TODO renommer les dsl -> check la javadoc egalement :)
    protected String staticId;
    protected T equipment;
    private final String equipmentType;
    private final String fieldName;

    public DslEquipment(String equipmentType, String fieldName) {
        this.equipmentType = equipmentType;
        this.fieldName = fieldName;
    }

    public DslEquipment(IdentifiableType identifiableType, String fieldName) {
        this.equipmentType = identifiableType.toString();
        this.fieldName = fieldName;
    }

    public DslEquipment(IdentifiableType identifiableType) {
        this(identifiableType, "staticId");
    }

    public DslEquipment(String equipmentType) {
        this(equipmentType, "staticId");
    }

    public void addEquipment(String equipmentId, Function<String, T> equipmentSupplier) {
        staticId = equipmentId;
        equipment = equipmentSupplier.apply(staticId);
    }

    public boolean checkEquipmentData(Reporter reporter) {
        if (!hasStaticId()) {
            Reporters.reportFieldNotSet(reporter, fieldName);
            return false;
        } else if (equipment == null) {
            Reporters.reportStaticIdUnknown(reporter, fieldName, staticId, equipmentType);
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
