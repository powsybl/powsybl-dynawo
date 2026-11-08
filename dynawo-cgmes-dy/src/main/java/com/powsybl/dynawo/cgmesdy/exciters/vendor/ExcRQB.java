/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcRQB – French RQB exciter. CIM: ExcRQB
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcRQB(
    String id, String synchronousMachineId,
    double clmt, double lsmin, double mesu, double t4m, double tc,
    double tf, double tout, double ucmax, double ucmin, double ki, double xp
) { }
