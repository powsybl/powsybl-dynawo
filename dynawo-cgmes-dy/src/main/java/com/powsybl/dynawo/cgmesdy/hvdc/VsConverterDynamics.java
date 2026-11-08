/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.hvdc;
/**
 * VsConverterDynamics – Voltage source converter (VSC-HVDC) dynamics.
 * CIM: VsConverter / VSCDynamics
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record VsConverterDynamics(
    String id, String vsConverterId,
    double droop,      // Voltage/current droop (PU)
    double droopCompensation, // Compensation for droop
    double pPccControl,  // Active power control mode
    double qPccControl,  // Reactive power control mode
    double maxModulationIndex, // Maximum modulation index
    double maxValveCurrent     // Maximum valve current (PU)
) { }
