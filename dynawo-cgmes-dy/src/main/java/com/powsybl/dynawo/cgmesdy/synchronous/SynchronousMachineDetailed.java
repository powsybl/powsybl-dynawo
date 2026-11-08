/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.synchronous;

/**
 * Detailed synchronous machine base class — extends {@code RotatingMachineDynamics}
 * with saturation and field-current base-type parameters.
 *
 * <p>CIM class: {@code SynchronousMachineDetailed}
 * (sub-class of {@code SynchronousMachineDynamics → RotatingMachineDynamics}).
 * In practice, most CGMES files instantiate one of the two concrete sub-classes
 * ({@link SynchronousMachineEquivalentCircuit} or
 * {@link SynchronousMachineTimeConstantReactance}) rather than this base class
 * directly.</p>
 *
 * <h3>Saturation parameters</h3>
 * <ul>
 *   <li>{@code saturationFactor}    – SE(1.0): saturation factor at 1.0 PU terminal voltage</li>
 *   <li>{@code saturationFactor120} – SE(1.2): saturation factor at 1.2 PU terminal voltage</li>
 * </ul>
 *
 * <h3>{@code ifdBaseType} enum values (CIM: {@code SynchronousMachineIfdBaseKind})</h3>
 * <ul>
 *   <li>{@code ifag}    – field current base = air-gap line field current</li>
 *   <li>{@code iffl}    – field current base = full-load field current</li>
 *   <li>{@code ifnl}    – field current base = no-load rated voltage field current</li>
 * </ul>
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record SynchronousMachineDetailed(
    String id,
    String synchronousMachineId,

    // RotatingMachineDynamics
    double mBase,
    double damping,
    double inertia,
    double statorLeakageReactance,
    double statorResistance,

    // SynchronousMachineDetailed
    /** CIM {@code SynchronousMachineIfdBaseKind} enum value as a string. */
    String ifdBaseType,
    /** Saturation factor SE at 1.0 PU flux / terminal voltage. */
    double saturationFactor,
    /** Saturation factor SE at 1.2 PU flux / terminal voltage. */
    double saturationFactor120
) { }
