/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcBBC – BBC static exciter. CIM: ExcBBC
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcBBC(
    String id, String synchronousMachineId,
    double ka, double t1, double t2, double t3, double t4,
    double vrmin, double vrmax, double kn, double kp, double switch1,
    double efdmin, double efdmax, double xe
) { }
