/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow;

import com.powsybl.commons.reporter.Reporter;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public final class Reports {

    private Reports() {
    }

    public static Reporter createDynaFlowReporter(Reporter reporter, String networkId) {
        return reporter.createSubReporter("dynaflow",
                "Dynaflow loadflow on network '${networkId}'",
                "networkId", networkId);
    }

    public static Reporter createDynaFlowSecurityAnalysisReporter(Reporter reporter, String networkId) {
        return reporter.createSubReporter("dynaflowSa",
                "Dynaflow security analysis on network '${networkId}'",
                "networkId", networkId);
    }

    public static Reporter createDynaFlowTimelineReporter(Reporter reporter, String contingencyId) {
        return reporter.createSubReporter("dynaflowSaContingency",
                "Contingency '${contingencyId}'",
                "contingencyId", contingencyId);
    }
}
