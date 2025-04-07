/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.report.TypedValue;
import com.powsybl.dynawo.commons.DynawoVersion;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynawoSimulationReports {

    private static final String DYNAMIC_ID_FIELD = "dynamicId";
    private static final String MODEL_NAME_FIELD = "modelName";

    private DynawoSimulationReports() {
    }

    public static ReportNode createDynawoSimulationReportNode(ReportNode reportNode, String networkId) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.dynawoSimulation")
                .withUntypedValue("networkId", networkId)
                .add();
    }

    public static ReportNode createDynawoModelSupplierReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.jsonDynamicModels")
                .add();
    }

    public static ReportNode createDynawoEventModelSupplierReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.jsonEventModels")
                .add();
    }

    public static ReportNode createDynawoSimulationContextReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.dynawoModelsProcessing")
                .add();
    }

    public static void reportDuplicateDynamicId(ReportNode reportNode, String duplicateId, String modelName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.duplicateDynamicId")
                .withUntypedValue("duplicateId", duplicateId)
                .withUntypedValue(MODEL_NAME_FIELD, modelName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportDynawoVersionTooHigh(ReportNode reportNode, String modelName, String dynamicId, DynawoVersion modelVersion, DynawoVersion currentVersion) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.highDynawoVersion")
                .withUntypedValue("modelVersion", modelVersion.toString())
                .withUntypedValue("currentVersion", currentVersion.toString())
                .withUntypedValue(MODEL_NAME_FIELD, modelName)
                .withUntypedValue(DYNAMIC_ID_FIELD, dynamicId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportDynawoVersionTooLow(ReportNode reportNode, String modelName, String dynamicId, DynawoVersion modelVersion, DynawoVersion currentVersion, String endCause) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.lowDynawoVersion")
                .withUntypedValue("modelVersion", modelVersion.toString())
                .withUntypedValue("currentVersion", currentVersion.toString())
                .withUntypedValue("endCause", endCause)
                .withUntypedValue(MODEL_NAME_FIELD, modelName)
                .withUntypedValue(DYNAMIC_ID_FIELD, dynamicId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportEmptyAutomationSystem(ReportNode reportNode, String automationSystemName, String dynamicId, String equipmentId, String expectedModels) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.emptyAutomationSystem")
                .withUntypedValue("automationSystemName", automationSystemName)
                .withUntypedValue(DYNAMIC_ID_FIELD, dynamicId)
                .withUntypedValue("equipmentId", equipmentId)
                .withUntypedValue("expectedModels", expectedModels)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportEmptyListAutomationSystem(ReportNode reportNode, String automationSystemName, String dynamicId, String expectedModels) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.emptyListAutomationSystem")
                .withUntypedValue("automationSystemName", automationSystemName)
                .withUntypedValue(DYNAMIC_ID_FIELD, dynamicId)
                .withUntypedValue("expectedModels", expectedModels)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFailedDynamicModelHandling(ReportNode reportNode, String modelName, String dynamicId, String equipmentType) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.failedDynamicModelHandling")
                .withUntypedValue(MODEL_NAME_FIELD, modelName)
                .withUntypedValue(DYNAMIC_ID_FIELD, dynamicId)
                .withUntypedValue("equipmentType", equipmentType)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static ReportNode createDynawoSpecificLogReportNode(ReportNode reportNode, DynawoSimulationParameters.SpecificLog logType) {
        String key = switch (logType) {
            case NETWORK -> "dynawo.dynasim.dynawoNetworkLog";
            case MODELER -> "dynawo.dynasim.dynawoModelerLog";
            case PARAMETERS -> "dynawo.dynasim.dynawoParametersLog";
            case VARIABLES -> "dynawo.dynasim.dynawoVariablesLog";
            case EQUATIONS -> "dynawo.dynasim.dynawoEquationsLog";
        };
        return reportNode.newReportNode().withMessageTemplate(key).add();
    }

    public static void reportSpecificLogEntry(ReportNode reportNode, String logEntry) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.dynawoSpecificLogEntry")
                .withUntypedValue("message", logEntry)
                .add();
    }
}
