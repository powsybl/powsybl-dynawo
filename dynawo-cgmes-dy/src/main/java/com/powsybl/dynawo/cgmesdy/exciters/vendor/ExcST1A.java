/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcST1A – Static exciter ST1A (non-IEEE variant). CIM: ExcST1A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcST1A(
    String id, String synchronousMachineId,
    double tr, double vimax, double vimin, double tb, double tc,
    double ka, double ta, double vamax, double vamin,
    double vrmax, double vrmin, double kc, double kf, double tf,
    boolean pssin, boolean ilr, double klr
) { }
