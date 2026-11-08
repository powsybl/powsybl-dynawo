/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.gas;
/** GovGAST3 – Simplified gas turbine governor. CIM: GovGAST3
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovGAST3(
    String id, String synchronousMachineId,
    double mwbase, double r, double t1, double vmax, double vmin,
    double dturb, double bca, double kca, double tsi, double bp, double tsa, double tsb
) { }
