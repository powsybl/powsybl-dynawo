/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.psse.dyr.generators;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>GENTRA</b> – Transient Synchronous Generator model (2nd-order flux-decay model).
 *
 * <p>A simplified model that retains only transient (not sub-transient) rotor dynamics.
 * Suitable for screening studies or when sub-transient detail is unnecessary. Typically
 * applied to smaller units or in very large networks where reduced computation is needed.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'GENTRA' ID  T'do  T'qo  H  D  Xd  Xq  X'd  X'q  Xl  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "GENTRA"}.
 * @param machineId  Machine id.
 * @param tpdo       d-axis transient open-circuit time constant T'do [s].
 * @param tpqo       q-axis transient open-circuit time constant T'qo [s].
 * @param h          Inertia constant [MW·s/MVA].
 * @param d          Damping coefficient [pu].
 * @param xd         d-axis synchronous reactance Xd [pu].
 * @param xq         q-axis synchronous reactance Xq [pu].
 * @param xpd        d-axis transient reactance X'd [pu].
 * @param xpq        q-axis transient reactance X'q [pu].
 * @param xl         Leakage reactance Xl [pu].
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public record GentraRecord(
        int busNumber,
        String modelName,
        String machineId,

        // --- Time constants ---
        double tpdo,   // T'do [s]
        double tpqo,   // T'qo [s]

        // --- Mechanical ---
        double h,      // Inertia constant     [MW·s/MVA]
        double d,      // Damping coefficient  [pu]

        // --- Reactances ---
        double xd,     // Synchronous d-axis   [pu]
        double xq,     // Synchronous q-axis   [pu]
        double xpd,    // Transient d-axis     [pu]
        double xpq,    // Transient q-axis     [pu]
        double xl      // Leakage              [pu]
) implements DyrRecord { }
