/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.protection;
/** OverexcLimX – Extended overexcitation limiter. CIM: OverexcLimX
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record OverexcLimX(
    String id, String excitationSystemId,
    double efd1, double efd2, double efd3, double efddes, double efdrated,
    double kmx, double srew, double t1, double t2, double t3, double vlow
) { }
