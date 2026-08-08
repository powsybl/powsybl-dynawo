/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcELIN2 – ELIN exciter model 2. CIM: ExcELIN2
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcELIN2(
    String id, String synchronousMachineId,
    double tr4, double k1, double t1, double p1, double p2, double ti1, double ti3, double ti4,
    double te, double ermin, double ermax, double kcse, double kce, double tc4, double tb4,
    double efdbas, double iefmax, double iefmax2, double efmin2, double k2, double t2
) { }
