/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.simplifiers;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.report.TypedValue;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class SimplifierReports {

    private static final String EQUIPMENT_ID = "equipmentId";
    private static final String MODEL_NAME = "modelName";
    private static final String MODEL_ID = "modelId";

    private SimplifierReports() {
    }

    // Energized simplifier
    static ReportNode createEnergizedSimplifierReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.energizedSimplifier")
                .add();
    }

    static void reportDisconnectedTerminal(ReportNode reportNode, String modelName, String modelId, String equipmentId) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.disconnectedTerminal")
                .withUntypedValue(MODEL_NAME, modelName)
                .withUntypedValue(MODEL_ID, modelId)
                .withTypedValue(EQUIPMENT_ID, equipmentId, TypedValue.ID)
                .add();
    }

    static void reportVoltageLevelOff(ReportNode reportNode, String modelName, String modelId, String equipmentId) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.voltageLevelOff")
                .withUntypedValue(MODEL_NAME, modelName)
                .withUntypedValue(MODEL_ID, modelId)
                .withTypedValue(EQUIPMENT_ID, equipmentId, TypedValue.ID)
                .add();
    }

    // Main connected component simplifier
    static ReportNode createMainConnectedComponentSimplifierReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.mainConnectedComponentSimplifier")
                .add();
    }

    static void reportSubConnectedComponent(ReportNode reportNode, String modelName, String staticId) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.subConnectedComponent")
                .withUntypedValue(MODEL_NAME, modelName)
                .withTypedValue("staticId", staticId, TypedValue.ID)
                .add();
    }
}
