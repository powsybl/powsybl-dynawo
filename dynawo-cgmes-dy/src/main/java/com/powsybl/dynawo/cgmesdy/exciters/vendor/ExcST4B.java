/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcST4B – Static exciter ST4B (non-IEEE variant). CIM: ExcST4B
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcST4B(
    String id, String synchronousMachineId,
    double tr, double kpr, double kir, double ta, double vrmax, double vrmin,
    double kpm, double kim, double vmmax, double vmmin,
    double kg, double kp, double xl, double thetap, double vbmax, double kc
) { }
