/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.report.TypedValue;
import com.powsybl.iidm.network.IdentifiableType;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class BuilderReports {

    private static final String FIELD_NAME = "fieldName";

    private BuilderReports() {
    }

    public static void reportLibNotFound(ReportNode reportNode, String builderName, String lib) {
        reportNode.newReportNode()
                .withMessageTemplate("libNotFound", "Library ${lib} not found for ${builderName}")
                .withUntypedValue("builderName", builderName)
                .withUntypedValue("lib", lib)
                .withSeverity(TypedValue.INFO_SEVERITY)
                .add();
    }

    public static void reportModelInstantiation(ReportNode reportNode, String dynamicId) {
        reportNode.newReportNode()
                .withMessageTemplate("modelInstantiation", "Model ${dynamicId} instantiation successful")
                .withUntypedValue("dynamicId", dynamicId)
                .withSeverity(TypedValue.TRACE_SEVERITY)
                .add();
    }

    public static void reportModelInstantiationFailure(ReportNode reportNode, String dynamicId) {
        reportNode.newReportNode()
                .withMessageTemplate("modelInstantiation", "Model ${dynamicId} cannot be instantiated")
                .withUntypedValue("dynamicId", dynamicId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFieldReplacement(ReportNode reportNode, String fieldName, String replacementName, String replacement) {
        reportNode.newReportNode()
                .withMessageTemplate("fieldReplacement", "'${fieldName}' field is not set, ${replacementName} ${replacement} will be used instead")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue("replacementName", replacementName)
                .withUntypedValue("replacement", replacement)
                .withSeverity(TypedValue.TRACE_SEVERITY)
                .add();
    }

    public static void reportFieldNotSet(ReportNode reportNode, String fieldName) {
        reportNode.newReportNode()
                .withMessageTemplate("fieldNotSet", "'${fieldName}' field is not set")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportStaticIdUnknown(ReportNode reportNode, String fieldName, String staticId, String equipmentType) {
        reportNode.newReportNode()
                .withMessageTemplate("unknownStaticIdToDynamic", "'${fieldName}' field value '${staticId}' not found for equipment type(s) ${equipmentType}")
                .withUntypedValue("equipmentType", equipmentType)
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue("staticId", staticId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportDifferentNetwork(ReportNode reportNode, String fieldName, String staticId, String equipmentType) {
        reportNode.newReportNode()
                .withMessageTemplate("wrongNetwork", "'${fieldName}' field value ${equipmentType} ${staticId} does not belong to the builder network")
                .withUntypedValue("equipmentType", equipmentType)
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue("staticId", staticId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportUnknownStaticIdHandling(ReportNode reportNode, String fieldName, String staticId, String equipmentType) {
        reportNode.newReportNode()
                .withMessageTemplate("staticIdUnknown", "'${fieldName}' field value '${staticId}' not found for equipment type(s) ${equipmentType}, id will be used as pure dynamic model id")
                .withUntypedValue("equipmentType", equipmentType)
                .withUntypedValue(FIELD_NAME, fieldName).withUntypedValue("staticId", staticId)
                .withSeverity(TypedValue.INFO_SEVERITY)
                .add();
    }

    public static void reportCrossThreshold(ReportNode reportNode, String fieldName, double fieldValue, String threshold) {
        reportNode.newReportNode()
                .withMessageTemplate("crossThreshold", "${fieldName} should be ${threshold} (${fieldValue})")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withUntypedValue("fieldValue", fieldValue)
                .withUntypedValue("threshold", threshold)
                .withSeverity(TypedValue.WARN_SEVERITY).add();
    }

    public static void reportEmptyList(ReportNode reportNode, String fieldName) {
        reportNode.newReportNode()
                .withMessageTemplate("emptyList", "'${fieldName}' list is empty")
                .withUntypedValue(FIELD_NAME, fieldName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportFieldSetWithWrongEquipment(ReportNode reportNode, String fieldName, IdentifiableType equipmentType, String staticId) {
        reportFieldSetWithWrongEquipment(reportNode, fieldName, equipmentType.toString() + " " + staticId);
    }

    public static void reportFieldSetWithWrongEquipment(ReportNode reportNode, String fieldName, String equipment) {
        reportNode.newReportNode()
                .withMessageTemplate("fieldSetWithWrongEquipment", "'${fieldName}' field is set but ${equipment} does not possess this option")
                .withUntypedValue(FIELD_NAME, fieldName).withUntypedValue("equipment", equipment).withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

}
