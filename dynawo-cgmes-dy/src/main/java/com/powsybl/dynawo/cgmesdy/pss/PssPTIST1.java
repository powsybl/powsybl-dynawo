/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.pss;
/** PssPTIST1 – PTI stabilizer type 1. CIM: PssPTIST1
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record PssPTIST1(
    String id, String excitationSystemId,
    double dtc, double dtf, double dtp, double k, double m, double t1, double t2, double t3, double t4,
    double tp, double vsmn, double vsmx
) { }
