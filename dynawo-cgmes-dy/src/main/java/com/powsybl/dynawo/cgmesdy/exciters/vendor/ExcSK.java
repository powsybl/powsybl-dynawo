/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcSK – Slovak utility exciter. CIM: ExcSK
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcSK(
    String id, String synchronousMachineId,
    double k, double k1, double k2, double kc, double kce, double kd, double kgob, double kp,
    double kqi, double kqob, double kqp, double nq, double qconoff, double qz,
    double remote, double sbase, double tc, double te, double ti, double tp, double tr,
    double uimax, double uimin, double urmax, double urmin, double vtmax, double vtmin, double yp
) { }
