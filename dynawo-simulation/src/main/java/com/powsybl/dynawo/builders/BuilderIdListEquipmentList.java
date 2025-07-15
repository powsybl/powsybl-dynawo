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

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BuilderIdListEquipmentList<T extends Identifiable<?>> extends BuilderEquipmentsList<T> {

    public BuilderIdListEquipmentList(String equipmentType, String fieldName, ReportNode reportNode) {
        super(equipmentType, fieldName, reportNode);
    }

    public void addEquipments(Collection<String>[] staticIdsArray, Function<String, T> equipmentSupplier,
                              EquipmentChecker<T> equipmentChecker, StaticIdListUnknownReportNodeBuilder reportNodeBuilder) {
        for (Collection<String> staticIds : staticIdsArray) {
            addEquipment(staticIds, equipmentSupplier, equipmentChecker, reportNodeBuilder);
        }
        reportIfEmptyList();
    }

    private void addEquipment(Collection<String> staticIds, Function<String, T> equipmentSupplier,
                              EquipmentChecker<T> equipmentChecker, StaticIdListUnknownReportNodeBuilder reportNodeBuilder) {
        staticIds.stream()
                .map(equipmentSupplier)
                .filter(Objects::nonNull)
                .filter(eq -> equipmentChecker.test(eq, fieldName, reportNode))
                .findFirst()
                .ifPresentOrElse(equipments::add, () -> {
                    String ids = staticIds.toString();
                    missingEquipmentIds.add(ids);
                    reportNodeBuilder.buildReportNode(reportNode, fieldName, ids, equipmentType);
                });
    }
}
