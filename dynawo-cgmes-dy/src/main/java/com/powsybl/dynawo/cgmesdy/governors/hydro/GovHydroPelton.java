/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.hydro;
/** GovHydroPelton – Pelton turbine governor. CIM: GovHydroPelton
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovHydroPelton(
    String id, String synchronousMachineId,
    double mwbase, double av0, double av1, double bp, double db1,
    double db2, double h1, double h2, double hn, double kc, double kg,
    double qc0, double qn, double simplifiedPelton, double staticCompensating,
    double ta, double td, double ts, double twnc, double twng, double tx,
    double va, double valvmax, double valvmin, double vav, double vc, double vcv,
    boolean cfrac, boolean sfrac
) { }
