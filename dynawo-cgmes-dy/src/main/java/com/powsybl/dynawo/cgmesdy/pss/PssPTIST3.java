/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.pss;
/** PssPTIST3 – PTI stabilizer type 3. CIM: PssPTIST3
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record PssPTIST3(
    String id, String excitationSystemId,
    double a0, double a1, double a2, double a3, double a4, double a5,
    double al, double athres, double b0, double b1, double b2, double b3, double b4, double b5,
    double dl, double dtc, double dtf, double dtp, double isfreq, double k,
    double lthres, double m, double nav, double ncl, double pmin, double t1, double t2, double t3, double t4,
    double tp, double vsmn, double vsmx
) { }
