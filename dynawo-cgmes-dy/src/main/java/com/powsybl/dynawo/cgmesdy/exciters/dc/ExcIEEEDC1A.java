/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.dc;
/**
 * ExcIEEEDC1A – IEEE Type DC1A self-excited DC excitation system.
 * Ref: IEEE Std 421.5 Type DC1A. CIM: ExcIEEEDC1A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record ExcIEEEDC1A(
    String id, String synchronousMachineId,
    double tr,     // Filter time constant (s)
    double ka,     // Voltage regulator gain (PU)
    double ta,     // Voltage regulator time constant (s)
    double tb,     // Voltage regulator time constant (s)
    double tc,     // Voltage regulator time constant (s)
    double vrmax,  // Maximum output (PU)
    double vrmin,  // Minimum output (PU)
    double ke,     // Exciter field resistance constant
    double te,     // Exciter field time constant (s)
    double kf,     // Exciter rate feedback gain (PU)
    double tf,     // Exciter rate feedback lag time constant (s)
    double kc,     // Rectifier loading factor
    double kd,     // Demagnetizing factor
    double ki,     // Potential circuit gain (PU)
    double efd1,   // Exciter voltage at which saturation begins (PU)
    double seefd1, // Saturation function value at efd1 (PU)
    double efd2,   // Higher exciter voltage for saturation (PU)
    double seefd2, // Saturation function value at efd2 (PU)
    boolean uelin, // UEL input selector
    boolean exclim // Excitation limiters selector
) { }
