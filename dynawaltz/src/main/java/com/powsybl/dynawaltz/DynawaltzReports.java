/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.report.TypedValue;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynawaltzReports {

    private DynawaltzReports() {
    }

    public static ReportNode createDynaWaltzReportNode(ReportNode reportNode, String networkId) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawaltz", "Dynawaltz dynamic simulation on network '${networkId}'")
                .withUntypedValue("networkId", networkId)
                .add();
    }

    public static ReportNode createDynaWaltzContextReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawaltzContext", "Dynawaltz models processing")
                .add();
    }

    public static ReportNode createDynaWaltzTimelineReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawaltzTimeline", "Dynawaltz Timeline")
                .add();
    }

    public static void reportDuplicateStaticId(ReportNode reportNode, String duplicateId, String modelName, String dynamicId) {
        reportNode.newReportNode()
                .withMessageTemplate("duplicateStaticId", "Duplicate static id found: ${duplicateId} -> model ${modelName} ${dynamicId} will be skipped")
                .withUntypedValue("duplicateId", duplicateId)
                .withUntypedValue("modelName", modelName)
                .withUntypedValue("dynamicId", dynamicId)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportDuplicateDynamicId(ReportNode reportNode, String duplicateId, String modelName) {
        reportNode.newReportNode()
                .withMessageTemplate("duplicateDynamicId",
                        "Duplicate dynamic id found: ${duplicateId} -> model ${modelName} will be skipped")
                .withUntypedValue("duplicateId", duplicateId)
                .withUntypedValue("modelName", modelName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportEmptyAutomaton(ReportNode reportNode, String automatonName, String dynamicId, String expectedModels) {
        reportNode.newReportNode()
                .withMessageTemplate("emptyAutomaton",
                        "${automatonName} ${dynamicId} equipment is not a ${expectedModels}, the automation system will be skipped")
                .withUntypedValue("automatonName", automatonName)
                .withUntypedValue("dynamicId", dynamicId)
                .withUntypedValue("expectedModels", expectedModels)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void reportEmptyListAutomaton(ReportNode reportNode, String automatonName, String dynamicId, String expectedModels) {
        reportNode.newReportNode()
                .withMessageTemplate("emptyListAutomaton",
                        "None of ${automatonName} ${dynamicId} equipments are ${expectedModels}, the automation system will be skipped")
                .withUntypedValue("automatonName", automatonName)
                .withUntypedValue("dynamicId", dynamicId)
                .withUntypedValue("expectedModels", expectedModels)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }
}
