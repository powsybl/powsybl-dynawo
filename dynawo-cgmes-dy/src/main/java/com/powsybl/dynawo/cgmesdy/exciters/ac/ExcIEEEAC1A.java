/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.ac;
/** ExcIEEEAC1A – IEEE Type AC1A field-controlled alternator excitation system. CIM: ExcIEEEAC1A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record ExcIEEEAC1A(
    String id, String synchronousMachineId,
    double tr, double tb, double tc, double ka, double ta,
    double vamax, double vamin, double te, double kf, double tf,
    double kc, double kd, double ke,
    double vfemax, double vrmax, double vrmin,
    double e1, double se1, double e2, double se2
) { }
