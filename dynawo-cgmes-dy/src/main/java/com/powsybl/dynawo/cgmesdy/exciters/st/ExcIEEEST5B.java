/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.st;
/** ExcIEEEST5B – IEEE Type ST5B with deadband. CIM: ExcIEEEST5B
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record ExcIEEEST5B(
    String id, String synchronousMachineId,
    double tr, double kc, double vrmax, double vrmin,
    double t1, double tb1, double tc1, double tb2, double tc2,
    double tob1, double toc1, double tob2, double toc2,
    double tub1, double tuc1, double tub2, double tuc2
) { }
