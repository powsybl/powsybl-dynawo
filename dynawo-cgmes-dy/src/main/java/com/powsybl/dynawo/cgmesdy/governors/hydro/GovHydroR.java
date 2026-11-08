/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.hydro;
/** GovHydroR – Hydro governor with regulation. CIM: GovHydroR
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovHydroR(
    String id, String synchronousMachineId,
    double mwbase, double r, double td, double tf, double tg, double tp, double tt, double tr,
    double velm, double pmax, double pmin, double aturb, double bturb, double tturb,
    double db1, double eps, double db2, double gv1, double pgv1,
    double gv2, double pgv2, double gv3, double pgv3, double gv4, double pgv4,
    double gv5, double pgv5
) { }
