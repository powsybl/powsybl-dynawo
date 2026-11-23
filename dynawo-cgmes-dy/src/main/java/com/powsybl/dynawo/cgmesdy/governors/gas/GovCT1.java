/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.gas;
/** GovCT1 – General IEEE turbine-governor (combined cycle). CIM: GovCT1
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovCT1(
    String id, String synchronousMachineId,
    double mwbase, double r, double rdown, double rup,
    double ta, double tact, double tb, double tc, double teng, double tf,
    double tsa, double tsb, double vmax, double vmin,
    double wfnl, boolean wfspd,
    double kdgov, double kigov, double kpgov, double kpload, double kiload,
    double tdgov, double tno, double ldref, double dm, double db,
    double ropen, double rclose, double kimw, double pmwset, double aset, double ka
) { }
