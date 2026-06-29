/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.protection;
/** UnderexcLimIEEE1 – IEEE underexcitation limiter type 1. CIM: UnderexcLimIEEE1
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record UnderexcLimIEEE1(
    String id, String excitationSystemId,
    double kur, double kuc, double kuf, double vurmax, double vuimax, double vuimin,
    double tu1, double tu2, double tu3, double tu4
) { }
