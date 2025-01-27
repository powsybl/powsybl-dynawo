/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.report.TypedValue;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class MarginCalculationReports {

    private MarginCalculationReports() {
    }

    public static ReportNode createMarginCalculationReportNode(ReportNode reportNode, String networkId) {
        return reportNode.newReportNode()
                .withMessageTemplate("mc", "Dynawo margin calculation on network '${networkId}'")
                .withUntypedValue("networkId", networkId)
                .add();
    }

    public static void reportLoadVariationInstantiationFailure(ReportNode reportNode) {
        reportNode.newReportNode()
                .withMessageTemplate("loadsVariationInstantiationError", "LoadVariation cannot be instantiated")
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }
}
