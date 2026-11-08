/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.st;
/** ExcIEEEST7B – IEEE Type ST7B microprocessor-based excitation. CIM: ExcIEEEST7B
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record ExcIEEEST7B(
    String id, String synchronousMachineId,
    double tr, double kh, double kia, double tia, double tb, double tc,
    double tf, double kl, double kpa, double vrmax, double vrmin,
    double vmax, double vmin, double tg,
    String uelin, String oelin
) { }
