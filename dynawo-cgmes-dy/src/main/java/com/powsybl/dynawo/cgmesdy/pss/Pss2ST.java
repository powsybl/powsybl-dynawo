/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.pss;
/** Pss2ST – PSS with 2-stage torsional filter. CIM: Pss2ST
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record Pss2ST(
    String id, String excitationSystemId,
    String inputSignal1Type, String inputSignal2Type,
    double k1, double k2, double t1, double t2, double t3, double t4,
    double t5, double t6, double t7, double t8, double t9, double t10,
    double lsmax, double lsmin
) { }
