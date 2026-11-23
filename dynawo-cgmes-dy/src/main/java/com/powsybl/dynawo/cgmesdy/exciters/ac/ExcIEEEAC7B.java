/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.ac;
/** ExcIEEEAC7B – IEEE Type AC7B brushless excitation system. CIM: ExcIEEEAC7B
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record ExcIEEEAC7B(
    String id, String synchronousMachineId,
    double tr, double kpr, double kir, double kdr, double tdr,
    double vrmax, double vrmin, double kpa, double kia, double kda, double tda,
    double vamax, double vamin, double kp, double kl, double te, double ke,
    double vfemax, double vemin, double kc, double kd, double kf1, double kf2, double kf3, double tf,
    double e1, double se1, double e2, double se2,
    boolean uelin, boolean oelin
) { }
