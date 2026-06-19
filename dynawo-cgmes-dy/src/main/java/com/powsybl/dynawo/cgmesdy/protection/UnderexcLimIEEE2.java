/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.protection;
/** UnderexcLimIEEE2 – IEEE underexcitation limiter type 2. CIM: UnderexcLimIEEE2
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record UnderexcLimIEEE2(
    String id, String excitationSystemId,
    double k1, double k2, double kfb, double kuf, double kui, double kul,
    double p0, double p1, double q0, double q1,
    double tu1, double tu2, double tu3, double tu4,
    double vuimax, double vuimin
) { }
