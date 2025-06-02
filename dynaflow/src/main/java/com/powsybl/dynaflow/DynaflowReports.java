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
}
