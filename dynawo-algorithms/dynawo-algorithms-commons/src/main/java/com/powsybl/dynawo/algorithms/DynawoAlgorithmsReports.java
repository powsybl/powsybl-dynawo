/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms;

import com.powsybl.commons.report.ReportNode;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynawoAlgorithmsReports {

    private DynawoAlgorithmsReports() {
    }

    public static ReportNode createContingencyVoltageIdNotFoundReportNode(ReportNode reportNode, String contingencyId, String voltageLevelId) {
        return reportNode.newReportNode()
                .withMessageTemplate("contingencyVlIdNotFound", "Voltage id '${voltageLevelId}' of contingency '${contingencyId}' not found, contingency will be skipped")
                .withUntypedValue("voltageLevelId", voltageLevelId)
                .withUntypedValue("contingencyId", contingencyId)
                .add();
    }

    public static ReportNode createNotSupportedContingencyTypeReportNode(ReportNode reportNode, String contingencyType) {
        return reportNode.newReportNode()
                .withMessageTemplate("contingencyNotSupported", "Contingency element '${contingencyType}' not supported, contingency will be skipped")
                .withUntypedValue("contingencyType", contingencyType)
                .add();
    }
}
