/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public final class DynaflowReports {

    private DynaflowReports() {
    }

    public static ReportNode createDynaFlowReportNode(ReportNode reportNode, String networkId) {
        return reportNode.newReportNode()
                .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME)
                .withMessageTemplate("dynawo.dynaflow.loadflow")
                .withUntypedValue("networkId", networkId)
                .add();
    }

    public static ReportNode createDynaFlowSecurityAnalysisReportNode(ReportNode reportNode, String networkId) {
        return reportNode.newReportNode()
                .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME)
                .withMessageTemplate("dynawo.dynaflow.sa")
                .withUntypedValue("networkId", networkId)
                .add();
    }

    public static void createSidedContingencyReportNode(ReportNode reportNode, String contingencyId, String contingencyElementId) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.dynaflow.sidedContingency")
                .withUntypedValue("contingencyId", contingencyId)
                .withUntypedValue("contingencyElementId", contingencyElementId)
                .add();
    }
}
