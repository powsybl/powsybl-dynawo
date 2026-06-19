/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcAC6A – Non-IEEE AC6A stationary rectifier variant. CIM: ExcAC6A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcAC6A(
    String id, String synchronousMachineId,
    double tr, double ka, double ta, double vamax, double vamin, double tk, double tb, double tc,
    double te, double ke, double vhmax, double kh, double tj, double th, double td,
    double kc, double kd, double vrmax, double vrmin, double e1, double se1, double e2, double se2
) { }
