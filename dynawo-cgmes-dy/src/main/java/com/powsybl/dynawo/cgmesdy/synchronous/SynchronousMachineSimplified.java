/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.synchronous;

/**
 * Classical (constant-voltage-behind-Xd') machine model.
 *
 * <p>CIM class: {@code SynchronousMachineSimplified}
 * (sub-class of {@code SynchronousMachineDynamics → RotatingMachineDynamics}).</p>
 *
 * <p>This model carries only the base {@code RotatingMachineDynamics} electrical
 * parameters; it adds no extra parameters of its own beyond the association
 * to the steady-state {@code SynchronousMachine}.</p>
 *
 * <h3>Parameter units (all per-unit on machine MVA base unless noted)</h3>
 * <ul>
 *   <li>{@code mBase}                 – machine MVA rating (MVA)</li>
 *   <li>{@code damping}               – damping torque coefficient D (PU torque / PU speed)</li>
 *   <li>{@code inertia}               – inertia constant H (MWs/MVA)</li>
 *   <li>{@code statorLeakageReactance}– armature leakage reactance Xl (PU)</li>
 *   <li>{@code statorResistance}      – armature resistance Ra (PU)</li>
 * </ul>
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record SynchronousMachineSimplified(
    /** RDF resource identifier (from the DY profile). */
    String id,
    /** URI reference to the associated {@code SynchronousMachine} (steady-state EQ profile). */
    String synchronousMachineId,

    // RotatingMachineDynamics base attributes
    /** Machine MVA rating (MVA). {@code Double.NaN} when absent in the file. */
    double mBase,
    /** Damping torque coefficient D (PU torque / PU speed deviation). */
    double damping,
    /** Inertia constant H (MWs / MVA). */
    double inertia,
    /** Armature (stator) leakage reactance Xl (PU). */
    double statorLeakageReactance,
    /** Armature (stator) resistance Ra (PU). */
    double statorResistance
) { }
