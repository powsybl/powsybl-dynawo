/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.dc;
/** ExcIEEEDC4B – IEEE Type DC4B with PID regulator. CIM: ExcIEEEDC4B
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record ExcIEEEDC4B(
    String id, String synchronousMachineId,
    double tr, double ka, double ta, double kp, double ki, double kd, double td,
    double vrmax, double vrmin, double ke, double te, double kf, double tf,
    double kc, double ki2, double vfemax, double vemin,
    double efd1, double seefd1, double efd2, double seefd2,
    boolean uelin, boolean oelin
) { }
