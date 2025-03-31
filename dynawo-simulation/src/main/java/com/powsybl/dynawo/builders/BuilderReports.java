/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.report.TypedValue;
import com.powsybl.iidm.network.IdentifiableType;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class BuilderReports {

    private static final String FIELD_NAME = "fieldName";
    private static final String EQUIPMENT_TYPE_FIELD = "equipmentType";

    private BuilderReports() {
    }

    public static ReportNode createModelInstantiationReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.modelInstantiation", "Model ${modelName} ${dynamicId} instantiation ${state}")
                .add();
    }

    public static void setupModelInstantiation(ReportNode reportNode, String modelName, String dynamicId,
                                               boolean isInstantiable) {
        reportNode.addUntypedValue("modelName", modelName)
                .addUntypedValue("dynamicId", dynamicId != null ? dynamicId : "null");
        if (isInstantiable) {
            reportNode.addUntypedValue("state", "successful")
                    .addSeverity(TypedValue.INFO_SEVERITY);
        } else {
            reportNode.addUntypedValue("state", "failed")
                    .addSeverity(TypedValue.WARN_SEVERITY);
        }
    }

    public static void reportBuilderNotFound(ReportNode reportNode, String lib) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.builderNotFound", "No builder found for ${lib}")
                .withUntypedValue("lib", lib)
                .withSeverity(TypedValue.INFO_SEVERITY)
                .add();
    }

    public static void reportModelNotFound(ReportNode reportNode, String builderName, String modelName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.modelNotFound", "Model ${lib} not found for ${builderName}")
                .withUntypedValue("builderName", builderName)
                .withUntypedValue("modelName", modelName)
                .withSeverity(TypedValue.INFO_SEVERITY)
                .add();
    }

    public static void reportOutputVariableInstantiationFailure(ReportNode reportNode, String id) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.outputVariableInstantiationError", "Output variable ${id} cannot be instantiated")
                .withUntypedValue("id", id)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFieldNotSet(ReportNode reportNode, String fieldName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.fieldNotSet", "'${fieldName}' field is not set")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportStaticIdUnknown(ReportNode reportNode, String fieldName, String staticId, String equipmentType) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.unknownIdToDynamic", "'${fieldName}' field value '${staticId}' not found for equipment type(s) ${equipmentType}")
                .withUntypedValue(EQUIPMENT_TYPE_FIELD, equipmentType)
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue("staticId", staticId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportDifferentNetwork(ReportNode reportNode, String fieldName, String staticId, String equipmentType) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.wrongNetwork", "'${fieldName}' field value ${equipmentType} ${staticId} does not belong to the builder network")
                .withUntypedValue(EQUIPMENT_TYPE_FIELD, equipmentType)
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue("staticId", staticId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportUnknownStaticIdHandling(ReportNode reportNode, String fieldName, String staticId, String equipmentType) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.staticIdUnknown", "'${fieldName}' field value '${staticId}' not found for equipment type(s) ${equipmentType}, id will be used as pure dynamic model id")
                .withUntypedValue(EQUIPMENT_TYPE_FIELD, equipmentType)
                .withUntypedValue(FIELD_NAME, fieldName).withUntypedValue("staticId", staticId)
                .withSeverity(TypedValue.INFO_SEVERITY)
                .add();
    }

    public static void reportCrossThreshold(ReportNode reportNode, String fieldName, double fieldValue, String threshold) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.crossThreshold", "${fieldName} should be ${threshold} (${fieldValue})")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue("fieldValue", fieldValue)
                .withUntypedValue("threshold", threshold)
                .withSeverity(TypedValue.WARN_SEVERITY).add();
    }

    public static void reportEmptyList(ReportNode reportNode, String fieldName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.emptyList", "'${fieldName}' list is empty")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFieldSetWithWrongEquipment(ReportNode reportNode, String fieldName, IdentifiableType equipmentType, String staticId) {
        reportFieldSetWithWrongEquipment(reportNode, fieldName, equipmentType.toString() + " " + staticId);
    }

    public static void reportFieldSetWithWrongEquipment(ReportNode reportNode, String fieldName, String equipment) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.fieldSetWithWrongEquipment", "'${fieldName}' field is set but ${equipment} does not possess this option")
                .withUntypedValue(FIELD_NAME, fieldName).withUntypedValue("equipment", equipment).withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFieldOptionNotImplemented(ReportNode reportNode, String fieldName, String defaultValue) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.fieldOptionNotImplemented", "'${fieldName}' field is set but this option is not implemented yet, default value ${defaultValue} will be used")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue("defaultValue", defaultValue)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFieldConflict(ReportNode reportNode, String firstFieldName, String secondFieldName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.fieldConflict", "Both '${firstFieldName}' and '${secondFieldName}' are defined, '${firstFieldName}' will be used")
                .withUntypedValue("firstFieldName", firstFieldName)
                .withUntypedValue("secondFieldName", secondFieldName)
                .withSeverity(TypedValue.TRACE_SEVERITY)
                .add();
    }
}
