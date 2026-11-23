/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.steam;
/** GovSteamFV2 – Steam governor with fast valving. CIM: GovSteamFV2
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovSteamFV2(
    String id, String synchronousMachineId,
    double mwbase, double r, double t1, double vamax, double vamin,
    double k, double t2, double t3, double pmax, double pmin,
    double t4, double k1, double k2, double t5, double k3, double k4,
    int iFlag
) { }
