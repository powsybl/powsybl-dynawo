/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcPIC – Proportional-integral controller exciter. CIM: ExcPIC
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcPIC(
    String id, String synchronousMachineId,
    double ka, double ta, double kf, double tf1, double tf2, double e1, double se1, double e2, double se2,
    double efdmax, double efdmin, double ka2, double vr1, double vr2,
    double t1, double t2, double t3, double t4, double t5, double vrmax, double vrmin
) { }
