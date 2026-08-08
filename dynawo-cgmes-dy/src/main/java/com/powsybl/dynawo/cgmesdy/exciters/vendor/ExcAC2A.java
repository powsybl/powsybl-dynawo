/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcAC2A – Non-IEEE AC2A high initial response variant. CIM: ExcAC2A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcAC2A(
    String id, String synchronousMachineId,
    double tr, double tb, double tc, double ka, double ta, double vamax, double vamin,
    double kb, double vrmax, double vrmin, double te, double vfemax, double kh, double kf, double tf,
    double kc, double kd, double ke, double e1, double se1, double e2, double se2
) { }
