/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.steam;
/** GovSteamCC – Combined cycle steam governor. CIM: GovSteamCC
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovSteamCC(
    String id, String synchronousMachineId,
    double mwbase, double r, double t1, double t2, double t3, double t4,
    double pmax, double pmin, double uc, double uo,
    double dhp, double dlp, double fhp, double flp, double fip,
    double tip, double tlp
) { }
