/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.synchronous;

/**
 * Time-constant / reactance parameterisation — the most widely used form
 * in large-scale CGMES DY profile exchanges (IEEE Std 1110 naming convention).
 *
 * <p>CIM class: {@code SynchronousMachineTimeConstantReactance}
 * (sub-class of {@code SynchronousMachineDetailed → SynchronousMachineDynamics
 *  → RotatingMachineDynamics}).</p>
 *
 * <h3>modelType enum ({@code SynchronousMachineModelKind})</h3>
 * <ul>
 *   <li>{@code subtransient} – Xd, Xd', Xd'', Xq, Xq', Xq'', T'd0, T''d0, T'q0, T''q0 are all used.</li>
 *   <li>{@code transient}    – Only Xd, Xd', Xq, T'd0, T'q0 are meaningful; subtransient values unused.</li>
 * </ul>
 *
 * <h3>Reactance parameter ordering (physical constraint)</h3>
 * <pre>
 *   Xd  ≥ Xd'  ≥ Xd'' ≥ Xl
 *   Xq  ≥ Xq'' ≥ Xl
 * </pre>
 *
 * <h3>Time-constant parameter ordering (physical constraint)</h3>
 * <pre>
 *   T'd0  ≥ T''d0
 *   T'q0  ≥ T''q0
 * </pre>
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record SynchronousMachineTimeConstantReactance(
    String id,
    String synchronousMachineId,

    // RotatingMachineDynamics
    double mBase,
    double damping,
    double inertia,
    double statorLeakageReactance,
    double statorResistance,

    // SynchronousMachineDetailed
    /** CIM {@code SynchronousMachineIfdBaseKind} enum value (e.g. {@code ifag}). */
    String ifdBaseType,
    /** Saturation factor SE at 1.0 PU flux. */
    double saturationFactor,
    /** Saturation factor SE at 1.2 PU flux. */
    double saturationFactor120,

    // SynchronousMachineTimeConstantReactance
    /**
     * Model complexity: {@code "subtransient"} or {@code "transient"}.
     * CIM attribute: {@code SynchronousMachineTimeConstantReactance.modelType}.
     */
    String modelType,
    /**
     * Saturation loading correction factor Ks (PU).
     * Shifts the saturation characteristic origin; typically 0.
     */
    double ks,

    // d-axis reactances (PU on mBase)
    /** Synchronous d-axis reactance Xd (PU). */
    double xDirectSync,
    /** Transient d-axis reactance Xd' (PU). */
    double xDirectTrans,
    /** Subtransient d-axis reactance Xd'' (PU). */
    double xDirectSubtrans,

    // q-axis reactances (PU on mBase)
    /** Synchronous q-axis reactance Xq (PU). */
    double xQuadSync,
    /** Transient q-axis reactance Xq' (PU). */
    double xQuadTrans,
    /** Subtransient q-axis reactance Xq'' (PU). */
    double xQuadSubtrans,

    // d-axis open-circuit time constants (seconds)
    /** d-axis transient open-circuit time constant T'd0 (s). */
    double tpdo,
    /** d-axis subtransient open-circuit time constant T''d0 (s). */
    double tppdo,

    // q-axis open-circuit time constants (seconds)
    /** q-axis transient open-circuit time constant T'q0 (s). */
    double tpqo,
    /** q-axis subtransient open-circuit time constant T''q0 (s). */
    double tppqo,

    /** Armature leakage reactance Xl (PU). Must satisfy Xl ≤ Xd''. */
    double xl
) { }
