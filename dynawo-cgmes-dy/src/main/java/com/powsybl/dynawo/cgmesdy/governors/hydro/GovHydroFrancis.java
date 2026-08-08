/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.hydro;
/** GovHydroFrancis – Francis turbine governor. CIM: GovHydroFrancis
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovHydroFrancis(
    String id, String synchronousMachineId,
    double mwbase, double rs, double tg, double tp, double bp, double td,
    double ta, double ts, double twnc, double twng, double qn, double h0,
    double am, double av0, double avsmnx, double avsmx, double hn,
    double kc, double kg, double ki, double knl, double qc0, double va,
    double valvmax, double valvmin, double vc,
    String waterTunnelSurgeChamberSimulation
) { }
