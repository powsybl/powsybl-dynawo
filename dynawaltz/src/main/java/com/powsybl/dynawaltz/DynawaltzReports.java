/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.reporter.Report;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.commons.reporter.TypedValue;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynawaltzReports {

    private DynawaltzReports() {
    }

    public static Reporter createDynaWaltzReporter(Reporter reporter, String networkId) {
        return reporter.createSubReporter("dynawaltz",
                "Dynawaltz dynamic simulation on network '${networkId}'",
                "networkId", networkId);
    }

    public static Reporter createDynaWaltzContextReporter(Reporter reporter) {
        return reporter.createSubReporter("dynawaltzContext",
                "Dynawaltz models processing");
    }

    public static Reporter createDynaWaltzTimelineReporter(Reporter reporter) {
        return reporter.createSubReporter("dynawaltzTimeline",
                "Dynawaltz Timeline");
    }

    public static void reportDuplicateStaticId(Reporter reporter, String duplicateId, String modelName, String dynamicId) {
        reporter.report(Report.builder()
                .withKey("duplicateStaticId")
                .withDefaultMessage("Duplicate static id found: ${duplicateId} -> model ${modelName} ${dynamicId} will be skipped")
                .withValue("duplicateId", duplicateId)
                .withValue("modelName", modelName)
                .withValue("dynamicId", dynamicId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }

    public static void reportDuplicateDynamicId(Reporter reporter, String duplicateId, String modelName) {
        reporter.report(Report.builder()
                .withKey("duplicateDynamicId")
                .withDefaultMessage("Duplicate dynamic id found: ${duplicateId} -> model ${modelName} will be skipped")
                .withValue("duplicateId", duplicateId)
                .withValue("modelName", modelName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }

    public static void reportEmptyAutomaton(Reporter reporter, String automatonName, String dynamicId, String expectedModels) {
        reporter.report(Report.builder()
                .withKey("emptyAutomaton")
                .withDefaultMessage("${automatonName} ${dynamicId} equipment is not a ${expectedModels}, the automaton will be skipped")
                .withValue("automatonName", automatonName)
                .withValue("dynamicId", dynamicId)
                .withValue("expectedModels", expectedModels)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }

    public static void reportEmptyListAutomaton(Reporter reporter, String automatonName, String dynamicId, String expectedModels) {
        reporter.report(Report.builder()
                .withKey("emptyListAutomaton")
                .withDefaultMessage("None of ${automatonName} ${dynamicId} equipments are ${expectedModels}, the automaton will be skipped")
                .withValue("automatonName", automatonName)
                .withValue("dynamicId", dynamicId)
                .withValue("expectedModels", expectedModels)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }
}
