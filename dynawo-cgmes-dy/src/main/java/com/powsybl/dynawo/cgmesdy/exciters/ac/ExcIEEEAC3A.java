/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.ac;
/** ExcIEEEAC3A – IEEE Type AC3A self-excited alternator. CIM: ExcIEEEAC3A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record ExcIEEEAC3A(
    String id, String synchronousMachineId,
    double tr, double tb, double tc, double ka, double ta,
    double vamax, double vamin, double te, double vemin,
    double kr, double kf, double tf, double kc, double kd, double ke, double kn,
    double vfemax, double efdn, double e1, double se1, double e2, double se2
) { }
