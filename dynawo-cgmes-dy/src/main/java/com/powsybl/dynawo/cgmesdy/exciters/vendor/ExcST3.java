/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcST3 – Static exciter ST3 variant. CIM: ExcST3
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcST3(
    String id, String synchronousMachineId,
    double tr, double vimax, double vimin, double ka, double ta, double tb, double tc,
    double vrmax, double vrmin, double km, double tm,
    double vmmax, double vmmin, double kg, double kp, double ki, double kc, double xl, double thetap, double vbmax
) { }
