package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.reporter.Report;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.commons.reporter.TypedValue;
import com.powsybl.iidm.network.IdentifiableType;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class Reporters {
    private Reporters() {
    }

    public static Reporter createModelBuilderReporter(Reporter reporter, String lib) {
        return reporter.createSubReporter("DSLModelBuilder", "DSL model builder for ${lib}", "lib", lib);
    }

    public static void reportModelInstantiation(Reporter reporter, String dynamicId) {
        reporter.report(Report.builder()
                .withKey("modelInstantiation")
                .withDefaultMessage("Model ${dynamicId} instantiation successful")
                .withValue("dynamicId", dynamicId)
                .withSeverity(TypedValue.TRACE_SEVERITY)
                .build());
    }

    public static void reportModelInstantiationFailure(Reporter reporter, String dynamicId) {
        reporter.report(Report.builder()
                .withKey("modelInstantiation")
                .withDefaultMessage("Model ${dynamicId} cannot be instantiated")
                .withValue("dynamicId", dynamicId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }

    public static void reportFieldReplacement(Reporter reporter, String fieldName, String replacementName, String replacement) {
        reporter.report(Report.builder()
                .withKey("fieldReplacement")
                .withDefaultMessage("'${fieldName}' field is not set, ${replacementName} ${replacement} will be used instead")
                .withValue("fieldName", fieldName)
                .withValue("replacementName", replacementName)
                .withValue("replacement", replacement)
                .withSeverity(TypedValue.TRACE_SEVERITY)
                .build());
    }

    public static void reportFieldNotSet(Reporter reporter, String fieldName) {
        reporter.report(Report.builder()
                .withKey("fieldNotSet")
                .withDefaultMessage("'${fieldName}' field is not set")
                .withValue("fieldName", fieldName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }

    public static void reportStaticIdUnknown(Reporter reporter, String fieldName, String staticId, String equipmentType) {
        reporter.report(Report.builder()
                .withKey("staticIdUnknown")
                .withDefaultMessage("'${fieldName}' field value '${staticId}' not found for equipment type(s) ${equipmentType}")
                .withValue("equipmentType", equipmentType)
                .withValue("fieldName", fieldName).withValue("staticId", staticId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }

    public static void reportCrossThreshold(Reporter reporter, String fieldName, double fieldValue, String threshold) {
        reporter.report(Report.builder()
                .withKey("crossThreshold")
                .withDefaultMessage("${fieldName} should be ${threshold} (${fieldValue})")
                .withValue("fieldName", fieldName)
                .withValue("fieldValue", fieldValue)
                .withValue("threshold", threshold)
                .withSeverity(TypedValue.WARN_SEVERITY).build());
    }

    public static void reportEmptyList(Reporter reporter, String fieldName) {
        reporter.report(Report.builder()
                .withKey("emptyList")
                .withDefaultMessage("'${fieldName}' list is empty")
                .withValue("fieldName", fieldName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }

    public static void reportFieldSetWithWrongEquipment(Reporter reporter, String fieldName, IdentifiableType equipmentType, String staticId) {
        reportFieldSetWithWrongEquipment(reporter, fieldName, equipmentType.toString() + " " + staticId);
    }

    public static void reportFieldSetWithWrongEquipment(Reporter reporter, String fieldName, String equipment) {
        reporter.report(Report.builder()
                .withKey("fieldSetWithWrongEquipment")
                .withDefaultMessage("'${fieldName}' field is set but ${equipment} does not possess this option")
                .withValue("fieldName", fieldName).withValue("equipment", equipment).withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }

}
