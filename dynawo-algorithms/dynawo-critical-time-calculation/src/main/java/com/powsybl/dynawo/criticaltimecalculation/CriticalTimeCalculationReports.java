/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation;

import com.powsybl.commons.report.PowsyblCoreReportResourceBundle;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.report.TypedValue;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public final class CriticalTimeCalculationReports {

    private CriticalTimeCalculationReports() {
    }

    public static ReportNode createCriticalTimeCalculationReportNode(ReportNode reportNode, String networkId) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.crittimecalc.ctc")
                .withTypedValue("networkId", networkId, TypedValue.ID)
                .add();
    }

    public static ReportNode createCriticalTimeCalculationReportNode() {
        return ReportNode.newRootReportNode()
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME, PowsyblDynawoReportResourceBundle.BASE_NAME)
                .withMessageTemplate("dynawo.crittimecalc.criticalTimeCalculationTool")
                .build();
    }

    public static ReportNode createFailedNodeFaultReportNode(ReportNode reportNode) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.crittimecalc.failedNodeFault")
                .add();
    }
}
