/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcDC2A – Non-IEEE DC exciter variant 2. CIM: ExcDC2A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcDC2A(
    String id, String synchronousMachineId,
    double tr, double ka, double ta, double tb, double tc,
    double vrmax, double vrmin, double ke, double te, double kf, double tf,
    double efd1, double seefd1, double efd2, double seefd2, boolean exclim, double vlb
) { }
