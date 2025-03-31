/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow;

import com.powsybl.commons.report.ReportNode;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public final class DynaflowReports {

    private DynaflowReports() {
    }

    public static ReportNode createDynaFlowReportNode(ReportNode reportNode, String networkId) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynaflow.loadflow", "Dynaflow loadflow on network '${networkId}'")
                .withUntypedValue("networkId", networkId)
                .add();
    }

    public static ReportNode createDynaFlowSecurityAnalysisReportNode(ReportNode reportNode, String networkId) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynaflow.sa", "Dynaflow security analysis on network '${networkId}'")
                .withUntypedValue("networkId", networkId)
                .add();
    }

    public static void createSidedContingencyReportNode(ReportNode reportNode, String contingencyId) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynaflow.sidedContingency", "Contingency '${contingencyId}' has a voltageId information and cannot be handle by DynaFlow, the contingency will be skipped")
                .withUntypedValue("contingencyId", contingencyId)
                .add();
    }
}
