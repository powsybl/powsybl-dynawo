/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.asynchronous;
/**
 * AsynchronousMachineTimeConstantReactance – Standard induction motor (T/X form).
 * CIM: AsynchronousMachineTimeConstantReactance
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public record AsynchronousMachineTimeConstantReactance(
    String id,
    String asynchronousMachineId,
    double xs,      // Synchronous reactance (PU)
    double xp,      // Transient reactance (PU)
    double xpp,     // Subtransient reactance (PU)
    double tpo,     // Transient rotor time constant (s)
    double tppo,    // Subtransient rotor time constant (s)
    double xl       // Leakage reactance (PU)
) { }
