/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.commons.report.PowsyblCoreReportResourceBundle;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.report.TypedValue;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;

import java.util.Locale;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class MarginCalculationReports {

    private MarginCalculationReports() {
    }

    public static ReportNode createMarginCalculationReportNode(ReportNode reportNode, String networkId) {
        return reportNode.newReportNode()
                .withMessageTemplate("dynawo.margincalc.mc")
                .withTypedValue("networkId", networkId, TypedValue.ID)
                .add();
    }

    public static ReportNode createMarginCalculationToolReportNode() {
        return ReportNode.newRootReportNode()
                .withLocale(Locale.US)
                .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME, PowsyblDynawoReportResourceBundle.BASE_NAME)
                .withMessageTemplate("dynawo.margincalc.marginCalculationTool")
                .build();
    }

    public static void reportLoadVariationInstantiationFailure(ReportNode reportNode) {
        reportNode.newReportNode()
                .withMessageTemplate("dynawo.margincalc.loadsVariationInstantiationError")
                .withSeverity(TypedValue.WARN_SEVERITY)
                .add();
    }
}
