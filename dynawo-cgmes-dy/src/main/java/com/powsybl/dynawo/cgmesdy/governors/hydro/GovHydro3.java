/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.hydro;
/** GovHydro3 – PID hydro governor. CIM: GovHydro3
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovHydro3(
    String id, String synchronousMachineId,
    double mwbase, double r, double tr, double pmax, double pmin,
    double ki, double kg, double tp, double td,
    double aturb, double bturb, double tturb, double velm,
    double tw, double db1, double eps, double db2,
    double tt, double t1, double t2, double t3, double t4,
    double qnl, double rperm, double rtemp, double hdam,
    int govtype
) { }
