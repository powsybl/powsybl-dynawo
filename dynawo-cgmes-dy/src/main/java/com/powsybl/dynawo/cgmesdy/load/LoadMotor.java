/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.load;
/** LoadMotor – Induction motor load model. CIM: LoadMotor
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record LoadMotor(
    String id,
    double pfrac, double ls, double ra, double lp, double lpp,
    double tpo, double tppo, double h, double d,
    double vt, double tv, double tbkr, double lfac,
    double compPF, double vbrkr, double vc1off, double vc2off, double vc1on, double vc2on
) { }
