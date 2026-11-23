/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.steam;
/** GovSteamFV4 – Detailed steam governor with torsional. CIM: GovSteamFV4
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovSteamFV4(
    String id, String synchronousMachineId,
    double mwbase, double r, double t1, double t2, double t3, double t4,
    double pmax, double pmin, double k, double k1, double k2, double k3, double k4,
    double t5, double t6, double t7, double ta, double tc, double ty, double tt,
    double kf1, double kf2, double tf2,
    double rsmimn, double rsmimx, double vvmax, double vvmin,
    double cpsmn, double cpsmx, double dp, double lpsp, double ovex
) { }
