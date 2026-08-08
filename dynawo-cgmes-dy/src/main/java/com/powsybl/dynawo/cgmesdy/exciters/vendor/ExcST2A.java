/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcST2A – Static exciter ST2A (non-IEEE variant). CIM: ExcST2A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcST2A(
    String id, String synchronousMachineId,
    double tr, double ka, double ta, double vrmax, double vrmin,
    double te, double kf, double tf, double kp, double ki, double kc, double efdmax
) { }
