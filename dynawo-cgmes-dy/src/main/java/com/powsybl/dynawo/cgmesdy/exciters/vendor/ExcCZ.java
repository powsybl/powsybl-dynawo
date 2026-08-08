/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcCZ – Czech utility exciter. CIM: ExcCZ
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcCZ(
    String id, String synchronousMachineId,
    double ka, double ta, double tc, double vrmax, double vrmin,
    double ke, double te, double krb, double kp, double tp, double efdmax
) { }
