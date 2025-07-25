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
import com.powsybl.iidm.network.HvdcConverterStation;
import com.powsybl.iidm.network.IdentifiableType;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class BuilderReports {

    private static final String EQUIPMENT_TYPE_FIELD = "equipmentType";
    private static final String FIELD_NAME = "fieldName";
    private static final String MODEL_NAME = "modelName";
    private static final String STATIC_ID = "staticId";

    private BuilderReports() {
    }

    public static ReportNode createModelInstantiationReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.modelInstantiation")
                .add();
    }

    public static void setupModelInstantiation(ReportNode reportNode, String modelName, String dynamicId,
                                               boolean isInstantiable) {
        reportNode.addUntypedValue(MODEL_NAME, modelName)
                .addUntypedValue("dynamicId", dynamicId != null ? dynamicId : "null");
        if (isInstantiable) {
            reportNode.addUntypedValue("state", "OK")
                    .addSeverity(TypedValue.INFO_SEVERITY);
        } else {
            reportNode.addUntypedValue("state", "KO")
                    .addSeverity(TypedValue.WARN_SEVERITY);
        }
    }

    public static void reportBuilderNotFound(ReportNode reportNode, String modelName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.builderNotFound")
                .withUntypedValue(MODEL_NAME, modelName)
                .withSeverity(TypedValue.INFO_SEVERITY)
                .add();
    }

    public static void reportModelNotFound(ReportNode reportNode, String category, String modelName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.modelNotFound")
                .withUntypedValue("category", category)
                .withUntypedValue(MODEL_NAME, modelName)
                .withSeverity(TypedValue.INFO_SEVERITY)
                .add();
    }

    public static void reportOutputVariableInstantiationFailure(ReportNode reportNode, String id) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.outputVariableInstantiationError")
                .withUntypedValue("id", id)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFieldNotSet(ReportNode reportNode, String fieldName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.fieldNotSet")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportStaticIdUnknown(ReportNode reportNode, String fieldName, String staticId, String equipmentType) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.staticIdUnknown")
                .withUntypedValue(EQUIPMENT_TYPE_FIELD, equipmentType)
                .withUntypedValue(FIELD_NAME, fieldName)
                .withTypedValue(STATIC_ID, staticId, TypedValue.ID)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportDifferentNetwork(ReportNode reportNode, String fieldName, String staticId, String equipmentType) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.wrongNetwork")
                .withUntypedValue(EQUIPMENT_TYPE_FIELD, equipmentType)
                .withUntypedValue(FIELD_NAME, fieldName)
                .withTypedValue(STATIC_ID, staticId, TypedValue.ID)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportUnknownStaticIdAsDynamicId(ReportNode reportNode, String fieldName, String staticId, String equipmentType) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.unknownIdToDynamic")
                .withUntypedValue(EQUIPMENT_TYPE_FIELD, equipmentType)
                .withUntypedValue(FIELD_NAME, fieldName)
                .withTypedValue(STATIC_ID, staticId, TypedValue.ID)
                .withSeverity(TypedValue.INFO_SEVERITY)
                .add();
    }

    //TODO traduction
    public static void reportCrossThreshold(ReportNode reportNode, String fieldName, double fieldValue, String threshold) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.crossThreshold")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue("fieldValue", fieldValue)
                .withUntypedValue("threshold", threshold)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportEnergizedStaticIdListUnknown(ReportNode reportNode, String fieldName, String staticIds, String equipmentType) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.energizedStaticIdListUnknown")
                .withUntypedValue(EQUIPMENT_TYPE_FIELD, equipmentType)
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue("staticIds", staticIds)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportEmptyList(ReportNode reportNode, String fieldName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.emptyList")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFieldSetWithWrongEquipment(ReportNode reportNode, String fieldName, IdentifiableType equipmentType, String staticId) {
        reportFieldSetWithWrongEquipment(reportNode, fieldName, equipmentType.toString() + " " + staticId);
    }

    public static void reportFieldSetWithWrongEquipment(ReportNode reportNode, String fieldName, String equipment) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.fieldSetWithWrongEquipment")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue("equipment", equipment)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFieldOptionNotImplemented(ReportNode reportNode, String fieldName, String defaultValue) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.fieldOptionNotImplemented")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue("defaultValue", defaultValue)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFieldConflict(ReportNode reportNode, String firstFieldName, String secondFieldName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.fieldConflict")
                .withUntypedValue("firstFieldName", firstFieldName)
                .withUntypedValue("secondFieldName", secondFieldName)
                .withSeverity(TypedValue.TRACE_SEVERITY)
                .add();
    }

    public static void reportWrongHvdcType(ReportNode reportNode, String fieldName, String staticId,
                                           HvdcConverterStation.HvdcType hvdcType) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.wrongHvdcType")
                .withUntypedValue("type", hvdcType.toString())
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue(STATIC_ID, staticId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFictitiousEquipment(ReportNode reportNode, String fieldName, String staticId) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.fictitiousEquipment")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue(STATIC_ID, staticId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportNotEnergized(ReportNode reportNode, String fieldName, String staticId) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynasim.notEnergized")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withTypedValue(STATIC_ID, staticId, TypedValue.ID)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }
}
