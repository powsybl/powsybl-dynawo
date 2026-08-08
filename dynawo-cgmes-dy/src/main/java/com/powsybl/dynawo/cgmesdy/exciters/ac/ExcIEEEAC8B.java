/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.ac;
/** ExcIEEEAC8B – IEEE Type AC8B PID excitation system. CIM: ExcIEEEAC8B
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record ExcIEEEAC8B(
    String id, String synchronousMachineId,
    double tr, double kpr, double kir, double kdr, double tdr,
    double vrmax, double vrmin, double ka, double ta,
    double vamax, double vamin, double te, double ke,
    double vfemax, double vemin, double kc, double kd,
    double e1, double se1, double e2, double se2,
    boolean uelin
) { }
