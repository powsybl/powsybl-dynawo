/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security;

import com.powsybl.commons.reporter.Reporter;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class Reports {

    private Reports() {
    }

    public static Reporter createDynamicSecurityAnalysisReporter(Reporter reporter, String networkId) {
        return reporter.createSubReporter("dsa",
                "Dynawo dynamic security analysis on network '${networkId}'",
                "networkId", networkId);
    }
}
