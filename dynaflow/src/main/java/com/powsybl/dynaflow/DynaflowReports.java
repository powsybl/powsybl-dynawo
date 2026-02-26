/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.report.TypedValue;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public final class DynaflowReports {

    private DynaflowReports() {
    }

    public static ReportNode createDynaFlowReportNode(ReportNode reportNode, String networkId) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynaflow.loadflow")
                .withTypedValue("networkId", networkId, TypedValue.ID)
                .add();
    }

    public static ReportNode createDynaFlowSecurityAnalysisReportNode(ReportNode reportNode, String networkId) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynaflow.sa")
                .withTypedValue("networkId", networkId, TypedValue.ID)
                .add();
    }

    public static void createSidedContingencyReportNode(ReportNode reportNode, String contingencyId) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynaflow.sidedContingency")
                .withUntypedValue("contingencyId", contingencyId)
                .add();
    }

    public static ReportNode createCheckParameterReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynaflow.checkParameters")
                .add();
    }

    public static void createCriticalUnsupportedParameterReportNode(ReportNode reportNode, String parameterName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynaflow.criticalUnsupportedParameter")
                .withUntypedValue("parameterName", parameterName)
                .withSeverity(TypedValue.ERROR_SEVERITY)
                .add();
    }

    public static void createIgnoredParameterReportNode(ReportNode reportNode, String parameterName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynaflow.ignoredParameter")
                .withUntypedValue("parameterName", parameterName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void createReplacedParameterValueReportNode(ReportNode reportNode, String parameterName, String value, String replacingValue) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynaflow.replacedParameter")
                .withUntypedValue("parameterName", parameterName)
                .withUntypedValue("value", value)
                .withUntypedValue("replacingValue", replacingValue)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }

    public static void createIidmReplacedParameterReportNode(ReportNode reportNode, String parameterName) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynaflow.iidmParameter")
                .withUntypedValue("parameterName", parameterName)
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }
}
