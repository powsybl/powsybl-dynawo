/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.wind;
/** WindContPType3IEC – Active power control for Type 3. CIM: WindContPType3IEC
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record WindContPType3IEC(
    String id,
    double dpmax, double dprefmax, double dprefmin, double dthetamx, double kdtd, double kip,
    double kpp, double mplvrt, double omegaoffset, double pdtdmax, double rramp,
    double tdvs, double temin, double tpord, double tufilt, double tuscale,
    double twref, double udvs, double updip, double wdtd,
    double zeta, boolean recrossflag
) { }
