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
 * PSS/E <b>GENCLS</b> – Classical (constant voltage behind transient reactance) generator model.
 *
 * <p>The classical model represents a generator with a fixed internal voltage magnitude behind a
 * constant reactance. It is the simplest dynamic generator model and is primarily used for
 * machines that are electrically remote or for preliminary stability studies.
 *
 * <p>dyr file column order (after bus / model / id header):
 * <pre>
 *   BUSNUM 'GENCLS' ID  H  D  /
 * </pre>
 *
 * @param busNumber  Bus number to which the generator is connected.
 * @param modelName  Always {@code "GENCLS"}.
 * @param machineId  Generator identifier (PSS/E machine id, e.g. {@code "1"}).
 * @param h          Inertia constant (MW·s/MVA). Typical range: 1–10.
 * @param d          Speed damping coefficient (pu torque / pu speed deviation).
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public record GenclsRecord(
        int busNumber,
        String modelName,
        String machineId,

        // --- Dynamic parameters ---
        double h,   // Inertia constant [MW·s/MVA]
        double d    // Damping coefficient [pu]
) implements DyrRecord { }
