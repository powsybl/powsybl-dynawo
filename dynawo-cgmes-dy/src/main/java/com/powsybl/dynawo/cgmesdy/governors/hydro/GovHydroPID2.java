/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.hydro;
/** GovHydroPID2 – Extended PID hydro governor. CIM: GovHydroPID2
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovHydroPID2(
    String id, String synchronousMachineId,
    double mwbase, double r, double pmax, double pmin,
    double kp, double ki, double kd, double ta, double tb, double treg,
    double tw, double velmax, double velmin, double gmax, double gmin, double g0
) { }
