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
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
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
                .withKey("duplicateId")
                .withDefaultMessage("Duplicate static id found: ${duplicateId} -> model ${modelName} ${dynamicId} will be skipped")
                .withValue("duplicateId", duplicateId)
                .withValue("modelName", modelName)
                .withValue("dynamicId", dynamicId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }

    public static void reportDuplicateDynamicId(Reporter reporter, String duplicateId, String modelName) {
        reporter.report(Report.builder()
                .withKey("duplicateId")
                .withDefaultMessage("Duplicate dynamic id found: ${duplicateId} -> model ${modelName} will be skipped")
                .withValue("duplicateId", duplicateId)
                .withValue("modelName", modelName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }

    public static void reportEmptyTapChanger(Reporter reporter, String dynamicId) {
        reporter.report(Report.builder()
                .withKey("emptyTC")
                .withDefaultMessage("TapChangerAutomaton ${dynamicId} load does not possess a transformer, the automaton will be skipped")
                .withValue("dynamicId", dynamicId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }

    public static void reportEmptyTapChangerBlockingAutomaton(Reporter reporter, String dynamicId) {
        reporter.report(Report.builder()
                .withKey("emptyTCB")
                .withDefaultMessage("None of TapChangerBlockingAutomaton {} equipments are TapChangerModel, the automaton will be skipped")
                .withValue("dynamicId", dynamicId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .build());
    }
}
