/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl

import com.powsybl.commons.reporter.Report
import com.powsybl.commons.reporter.Reporter
import com.powsybl.commons.reporter.TypedValue
import com.powsybl.iidm.network.IdentifiableType

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
final class Reporters {

    private Reporters() {
    }

    static Reporter createModelBuilderReporter(Reporter reporter, String lib) {
         reporter.createSubReporter("DSLModelBuilder",
                'DSL model builder for ${lib}',
                "lib", lib)
    }

    static void reportModelInstantiation(Reporter reporter, String dynamicId) {
        reporter.report(Report.builder()
                .withKey("modelInstantiation")
                .withDefaultMessage('Model ${dynamicId} instantiation successful')
                .withValue("dynamicId", dynamicId)
                .withSeverity(TypedValue.TRACE_SEVERITY)
                .build())
    }

    static void reportModelInstantiationFailure(Reporter reporter, String dynamicId) {
        reporter.report(Report.builder()
                .withKey("modelInstantiation")
                .withDefaultMessage('Model ${dynamicId} cannot be instantiated')
                .withValue("dynamicId", dynamicId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build())
    }

    static void reportFieldReplacement(Reporter reporter, String fieldName, String replacementName, String replacement) {
        reporter.report(Report.builder()
                .withKey("fieldReplacement")
                .withDefaultMessage('\'${fieldName}\' field is not set, ${replacementName} ${replacement} will be used instead')
                .withValue("fieldName", fieldName)
                .withValue("replacementName", replacementName)
                .withValue("replacement", replacement)
                .withSeverity(TypedValue.TRACE_SEVERITY)
                .build())
    }

    static void reportFieldNotSet(Reporter reporter, String fieldName) {
        reporter.report(Report.builder()
                .withKey("fieldNotSet")
                .withDefaultMessage('\'${fieldName}\' field is not set')
                .withValue("fieldName", fieldName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build())
    }

    static void reportStaticIdUnknown(Reporter reporter, String fieldName, String staticId, String equipmentType) {
        reporter.report(Report.builder()
                .withKey("fieldNotSet")
                .withDefaultMessage('\'${fieldName}\' field value \'${staticId}\' not found for equipment type(s) ${equipmentType}')
                .withValue("equipmentType", equipmentType)
                .withValue("fieldName", fieldName)
                .withValue("staticId", staticId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build())
    }

    static void reportCrossThreshold(Reporter reporter, String fieldName, double fieldValue, String threshold) {
        reporter.report(Report.builder()
                .withKey("crossThreshold")
                .withDefaultMessage('${fieldName} should be ${threshold} (${fieldValue})')
                .withValue("fieldName", fieldName)
                .withValue("fieldValue", fieldValue)
                .withValue("threshold", threshold)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build())
    }

    static void reportEmptyList(Reporter reporter, String fieldName) {
        reporter.report(Report.builder()
                .withKey("emptyList")
                .withDefaultMessage('\'${fieldName}\' list is empty')
                .withValue("fieldName", fieldName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build())
    }

    static void reportFieldSetWithWrongEquipment(Reporter reporter, String fieldName, IdentifiableType equipmentType, String staticId) {
        reportFieldSetWithWrongEquipment(reporter, fieldName, equipmentType.toString() + " " + staticId)
    }

    static void reportFieldSetWithWrongEquipment(Reporter reporter, String fieldName, String equipment) {
        reporter.report(Report.builder()
                .withKey("fieldSetWithWrongEquipment")
                .withDefaultMessage('\'${fieldName}\' field is set but ${equipment} does not possess this option')
                .withValue("fieldName", fieldName)
                .withValue("equipment", equipment)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build())
    }
}
