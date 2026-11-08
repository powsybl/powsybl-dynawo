/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcAVR2 – European AVR Model 2. CIM: ExcAVR2
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcAVR2(
    String id, String synchronousMachineId,
    double ka, double ta, double tb, double tc, double te, double vrmn, double vrmx,
    double e1, double se1, double e2, double se2, double tr
) { }
