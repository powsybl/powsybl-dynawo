/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.steam;
/**
 * GovSteam1 – IEEE steam turbine-governor (single/double reheat).
 * CIM: IEC 61970-302 GovSteam1
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovSteam1(
    String id, String synchronousMachineId,
    double mwbase, double k, double t1, double t2, double t3,
    double uo, double uc, double pmax, double pmin,
    double t4, double k1, double k2,
    double t5, double k3, double k4,
    double t6, double k5, double k6,
    double t7, double k7, double k8,
    double db1, double eps, double db2,
    double gv1, double pgv1, double gv2, double pgv2,
    double gv3, double pgv3, double gv4, double pgv4,
    double gv5, double pgv5, double gv6, double pgv6
) { }
