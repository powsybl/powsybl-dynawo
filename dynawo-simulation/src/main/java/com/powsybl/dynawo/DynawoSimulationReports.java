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
                .withMessageTemplate("dynawoSimulation", "Dynawo dynamic simulation on network '${networkId}'")
                .withUntypedValue("networkId", networkId)
                .add();
    }

    public static ReportNode createDynawoSimulationContextReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawoModelsProcessing", "Dynawo models processing")
                .add();
    }

    public static void reportDuplicateDynamicId(ReportNode reportNode, String duplicateId, String modelName) {
        reportNode.newReportNode()
                .withMessageTemplate("duplicateDynamicId",
                        "Duplicate dynamic id found: ${duplicateId} -> model ${modelName} will be skipped")
                .withUntypedValue("duplicateId", duplicateId)
                .withUntypedValue(MODEL_NAME_FIELD, modelName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportDynawoVersionTooHigh(ReportNode reportNode, String modelName, String dynamicId, DynawoVersion modelVersion, DynawoVersion currentVersion) {
        reportNode.newReportNode()
                .withMessageTemplate("highDynawoVersion", "Model version ${modelVersion} is too high for the current dynawo version ${currentVersion} -> model ${modelName} ${dynamicId} will be skipped")
                .withUntypedValue("modelVersion", modelVersion.toString())
                .withUntypedValue("currentVersion", currentVersion.toString())
                .withUntypedValue(MODEL_NAME_FIELD, modelName)
                .withUntypedValue(DYNAMIC_ID_FIELD, dynamicId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportDynawoVersionTooLow(ReportNode reportNode, String modelName, String dynamicId, DynawoVersion modelVersion, DynawoVersion currentVersion, String endCause) {
        reportNode.newReportNode()
                .withMessageTemplate("lowDynawoVersion", "Model version ${modelVersion} is too low for the current dynawo version ${currentVersion} ({$endCauses}) -> model ${modelName} ${dynamicId} will be skipped")
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
                .withMessageTemplate("emptyAutomationSystem",
                        "${automationSystemName} ${dynamicId} equipment ${equipmentId} is not a ${expectedModels}, the automation system will be skipped")
                .withUntypedValue("automationSystemName", automationSystemName)
                .withUntypedValue(DYNAMIC_ID_FIELD, dynamicId)
                .withUntypedValue("equipmentId", equipmentId)
                .withUntypedValue("expectedModels", expectedModels)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportEmptyListAutomationSystem(ReportNode reportNode, String automationSystemName, String dynamicId, String expectedModels) {
        reportNode.newReportNode()
                .withMessageTemplate("emptyListAutomationSystem",
                        "None of ${automationSystemName} ${dynamicId} equipments are ${expectedModels}, the automation system will be skipped")
                .withUntypedValue("automationSystemName", automationSystemName)
                .withUntypedValue(DYNAMIC_ID_FIELD, dynamicId)
                .withUntypedValue("expectedModels", expectedModels)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFailedDynamicModelHandling(ReportNode reportNode, String modelName, String dynamicId, String equipmentType) {
        reportNode.newReportNode()
                .withMessageTemplate("failedDynamicModelHandling",
                        "${modelName} ${dynamicId} cannot handle ${equipmentType} dynamic model, the model will be skipped")
                .withUntypedValue(MODEL_NAME_FIELD, modelName)
                .withUntypedValue(DYNAMIC_ID_FIELD, dynamicId)
                .withUntypedValue("equipmentType", equipmentType)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static ReportNode createDynawoSpecificLogReportNode(ReportNode reportNode, DynawoSimulationParameters.SpecificLog logType) {
        return switch (logType) {
            case NETWORK -> reportNode.newReportNode().withMessageTemplate("dynawoNetworkLog", "Network log").add();
            case MODELER -> reportNode.newReportNode().withMessageTemplate("dynawoModelerLog", "Modeler log").add();
            case PARAMETERS -> reportNode.newReportNode().withMessageTemplate("dynawoParametersLog", "Parameters log").add();
            case VARIABLES -> reportNode.newReportNode().withMessageTemplate("dynawoVariablesLog", "Variables log").add();
            case EQUATIONS -> reportNode.newReportNode().withMessageTemplate("dynawoEquationsLog", "Equations log").add();
        };
    }

    public static void reportSpecificLogEntry(ReportNode reportNode, String logEntry) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawoSpecificLogEntry", "${message}")
                .withUntypedValue("message", logEntry)
                .add();
    }
}
