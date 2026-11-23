/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.gas;
/** GovCT2 – Extended general IEEE turbine-governor. CIM: GovCT2
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovCT2(
    String id, String synchronousMachineId,
    double mwbase, double r, double rdown, double rup,
    double ta, double tact, double tb, double tc, double teng, double tf,
    double tsa, double tsb, double vmax, double vmin,
    double wfnl, boolean wfspd,
    double kdgov, double kigov, double kpgov, double kpload, double kiload,
    double tdgov, double tno, double ldref, double dm, double db,
    double ropen, double rclose, double kimw, double pmwset, double aset, double ka,
    double flim1, double plim1, double flim2, double plim2,
    double flim3, double plim3, double flim4, double plim4,
    double flim5, double plim5, double flim6, double plim6,
    double flim7, double plim7, double flim8, double plim8,
    double flim9, double plim9, double flim10, double plim10,
    boolean prate, double uc, double uo
) { }
