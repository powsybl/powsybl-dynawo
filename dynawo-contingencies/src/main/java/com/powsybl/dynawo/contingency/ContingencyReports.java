/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.contingency;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.report.TypedValue;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ContingencyReports {

    private ContingencyReports() {
    }

    public static ReportNode createContingencyReportNode(ReportNode reportNode, String contingencyId, String status) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.contingency.contingencyCreation")
                .withUntypedValue("contingencyId", contingencyId)
                .withUntypedValue("status", status)
                .withSeverity(TypedValue.INFO_SEVERITY)
                .add();
    }
}
