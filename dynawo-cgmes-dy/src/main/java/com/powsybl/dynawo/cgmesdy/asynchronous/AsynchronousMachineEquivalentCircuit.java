/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.asynchronous;
/**
 * AsynchronousMachineEquivalentCircuit – Induction machine equivalent circuit form.
 * CIM: AsynchronousMachineEquivalentCircuit
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public record AsynchronousMachineEquivalentCircuit(
    String id,
    String asynchronousMachineId,
    double rr1,   // Damper 1 resistance (PU)
    double xr1,   // Damper 1 reactance (PU)
    double rr2,   // Damper 2 resistance (PU) - optional, Double for nullable
    double xr2,   // Damper 2 reactance (PU) - optional
    double xm,    // Magnetizing reactance (PU)
    double xs,    // Stator reactance (PU)
    double rs     // Stator resistance (PU)
) { }
