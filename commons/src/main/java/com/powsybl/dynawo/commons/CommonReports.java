/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.report.TypedValue;
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

    public static ReportNode createDynawoLogReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.commons.dynawoLog")
                .add();
    }

    public static ReportNode createDynawoTimelineReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.commons.dynawoTimeline")
                .add();
    }

    public static void reportEmptyTimeline(ReportNode reportNode) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.commons.dynawoTimelineEmpty")
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportTimelineEntry(ReportNode reportNode, TimelineEntry timelineEntry) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.commons.dynawoTimelineEntry")
                .withTypedValue("time", timelineEntry.time(), TIME_MS)
                .withTypedValue("identifiableId", timelineEntry.modelName(), ID)
                .withUntypedValue("message", timelineEntry.message())
                .withUntypedValue("priority", timelineEntry.priority())
                .withSeverity(TypedValue.TRACE_SEVERITY)
                .add();
    }

    public static void reportLogEntry(ReportNode reportNode, LogEntry logEntry) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.commons.dynawoLogEntry")
                .withUntypedValue("message", logEntry.message())
                .withSeverity(logEntry.severity())
                .add();
    }
}
