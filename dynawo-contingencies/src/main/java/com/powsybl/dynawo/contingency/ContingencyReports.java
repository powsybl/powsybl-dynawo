/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.contingency;

import com.powsybl.commons.report.ReportNode;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ContingencyReports {

    private ContingencyReports() {
    }

    public static ReportNode createContingenciesTimelineReportNode(ReportNode reportNode, String contingencyId) {
        return reportNode.newReportNode()
                .withMessageTemplate("saContingency", "Contingency '${contingencyId}'")
                .withUntypedValue("contingencyId", contingencyId)
                .add();
    }

    public static void createSidedContingencyReportNode(ReportNode reportNode, String contingencyId) {
        reportNode.newReportNode()
                .withMessageTemplate("saContingency", "Contingency '${contingencyId}' has a voltageId information and cannot be handle by DynaFlow, the contingency will be skipped")
                .withUntypedValue("contingencyId", contingencyId)
                .add();
    }
}
