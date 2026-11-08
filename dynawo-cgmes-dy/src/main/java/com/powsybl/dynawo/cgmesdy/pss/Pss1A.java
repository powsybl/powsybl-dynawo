/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.pss;
/** Pss1A – Single-input PSS (variant). CIM: Pss1A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record Pss1A(
    String id, String excitationSystemId,
    String inputSignalType, double a1, double a2, double ks,
    double t1, double t2, double t3, double t4, double t5, double t6,
    double tdelay, double vcl, double vcu, double vrmax, double vrmin
) { }
