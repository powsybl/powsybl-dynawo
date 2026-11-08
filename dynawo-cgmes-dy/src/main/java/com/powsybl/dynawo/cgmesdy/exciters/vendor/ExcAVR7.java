/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcAVR7 – European AVR Model 7. CIM: ExcAVR7
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcAVR7(
    String id, String synchronousMachineId,
    double a1, double a2, double a3, double a4, double a5, double a6,
    double k1, double k3, double k5, double t1, double t2, double t3, double t4, double t5, double t6,
    double vmax1, double vmax3, double vmax5, double vmin1, double vmin3, double vmin5
) { }
