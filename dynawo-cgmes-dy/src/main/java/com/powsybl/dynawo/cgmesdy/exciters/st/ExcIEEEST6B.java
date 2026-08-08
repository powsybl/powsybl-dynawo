/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.st;
/** ExcIEEEST6B – IEEE Type ST6B with OEL/UEL integration. CIM: ExcIEEEST6B
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record ExcIEEEST6B(
    String id, String synchronousMachineId,
    double tr, double vimax, double vimin, double kci, double kff,
    double kg, double kia, double klr, double km, double kpa, double kvd,
    double ilr, double tg, double ts, double tvd, double vamax, double vamin,
    double vrmax, double vrmin, String oelin
) { }
