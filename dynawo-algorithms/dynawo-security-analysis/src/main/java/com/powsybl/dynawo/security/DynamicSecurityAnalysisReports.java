/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynamicSecurityAnalysisReports {

    private DynamicSecurityAnalysisReports() {
    }

    public static ReportNode createDynamicSecurityAnalysisReportNode(ReportNode reportNode, String networkId) {
        return reportNode.newReportNode()
                .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME)
                .withMessageTemplate("dynawo.dynasa.dsa")
                .withUntypedValue("networkId", networkId)
                .add();
    }
}
