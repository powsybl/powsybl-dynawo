/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.pss;
/** PssIEEE3B – IEEE Type PSS3B dual-input PSS. CIM: PssIEEE3B
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record PssIEEE3B(
    String id, String excitationSystemId,
    String inputSignal1Type, String inputSignal2Type,
    double ks1, double ks2, double t1, double t2, double t3,
    double a1, double a2, double a3, double a4, double a5, double a6, double a7, double a8,
    double vsmax, double vsmin
) { }
