/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.wind;
/** WindContQIEC – Reactive power/voltage controller. CIM: WindContQIEC
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record WindContQIEC(
    String id,
    double iqh1, double iqmax, double iqmin, double iqpost, double kiq, double kiu,
    double kpq, double kpu, double kqv, double rdroop, double tpfilt, double tpost,
    double tqord, double tufilt, double udb1, double udb2, double umax, double umin,
    double uqdip, double uref0, double xdroop,
    String mconq, String mqfrt, String mqpri, String windLVRTQcontrolModeType
) { }
