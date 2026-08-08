/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.protection;
/** UnderexcLimX2 – Extended underexcitation limiter variant 2. CIM: UnderexcLimX2
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record UnderexcLimX2(
    String id, String excitationSystemId,
    double ki, double kuf, double kui, double kul,
    double p0, double p1, double q0, double q1,
    double tu1, double tu2, double tu3, double tu4,
    double vuimax, double vuimin
) { }
