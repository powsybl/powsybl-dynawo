/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.steam;
/** GovSteamEU – European simplified steam governor. CIM: GovSteamEU
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovSteamEU(
    String id, String synchronousMachineId,
    double mwbase, double r, double ke, double kfcor, double komegacor,
    double t1, double t2, double t3, double t4, double t5, double t6,
    double pmax, double pmin, double te, double tfp, double tvhp, double tvip,
    double chc, double cho, double db1, double eps, double db2,
    double simX
) { }
