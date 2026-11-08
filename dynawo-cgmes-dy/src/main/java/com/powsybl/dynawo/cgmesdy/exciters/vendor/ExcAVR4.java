/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcAVR4 – European AVR Model 4. CIM: ExcAVR4
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcAVR4(
    String id, String synchronousMachineId,
    double ka, double ta, double tb, double tc, double vrmn, double vrmx,
    double ke, double te, double kf, double tf1, double e1, double se1, double e2, double se2, double tr
) { }
