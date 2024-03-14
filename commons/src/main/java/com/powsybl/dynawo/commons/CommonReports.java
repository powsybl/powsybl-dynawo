/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.commons.reporter.Report;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.commons.reporter.TypedValue;
import com.powsybl.dynawo.commons.dynawologs.LogEntry;
import com.powsybl.dynawo.commons.timeline.TimelineEntry;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class CommonReports {

    private static final String TIME_MS = "timeMsTypedValue";
    private static final String ID = "idTypedValue";

    private CommonReports() {
    }

    public static Reporter createDynawoLogReporter(Reporter reporter) {
        return reporter.createSubReporter("dynawoLog", "Dynawo Log");
    }

    public static void reportTimelineEntry(Reporter reporter, TimelineEntry timelineEntry) {
        reporter.report(Report.builder()
                .withKey("dynawoTimelineEntry")
                .withDefaultMessage("[t=${time}] ${message} on equipment '${identifiableId}'")
                .withTypedValue("time", timelineEntry.time(), TIME_MS)
                .withTypedValue("identifiableId", timelineEntry.modelName(), ID)
                .withValue("message", timelineEntry.message())
                .withSeverity(TypedValue.TRACE_SEVERITY)
                .build());
    }

    public static void reportLogEntry(Reporter reporter, LogEntry logEntry) {
        reporter.report(Report.builder()
                .withKey("dynawoLogEntry")
                .withDefaultMessage("${message}")
                .withValue("message", logEntry.message())
                .withSeverity(logEntry.severity())
                .build());
    }
}
