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
import java.util.List;

/**
 * Equipment ids not found in the network are seen as dynamic ids for automatons and reported as such
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BuilderModelEquipmentsList<T extends Identifiable<?>> extends BuilderEquipmentsList<T> {

    protected List<String> dynamicModelIds = new ArrayList<>();

    public BuilderModelEquipmentsList(String equipmentType, String fieldName, ReportNode reportNode) {
        super(equipmentType, fieldName, reportNode);
    }

    @Override
    protected void handleMissingId(String staticId) {
        dynamicModelIds.add(staticId);
        BuilderReports.reportUnknownStaticIdHandling(reportNode, fieldName, staticId, equipmentType);
    }

    @Override
    protected void reportEmptyList() {
        if (equipments.isEmpty() && missingEquipmentIds.isEmpty()) {
            BuilderReports.reportEmptyList(reportNode, fieldName);
        }
    }

    @Override
    public boolean checkEquipmentData() {
        boolean emptyList = equipments.isEmpty();
        boolean emptyModelList = dynamicModelIds.isEmpty();
        if (emptyList && emptyModelList && missingEquipmentIds.isEmpty()) {
            BuilderReports.reportFieldNotSet(reportNode, fieldName);
            return false;
        }
        return !emptyList || !emptyModelList;
    }

    public List<String> getDynamicModelIds() {
        return dynamicModelIds;
    }
}
