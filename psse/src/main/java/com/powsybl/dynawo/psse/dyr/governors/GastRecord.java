/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.psse.dyr.governors;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>GAST</b> – Gas Turbine Governor Model.
 *
 * <p>Represents a single-shaft gas turbine and its associated speed governor.
 * Includes compressor-discharge time constant, temperature limiter and load limits.
 * Commonly applied to aeroderivative and heavy frame gas turbines.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'GAST' ID  R  T1  T2  T3  AT  KT  VMAX  VMIN  Dturb  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "GAST"}.
 * @param machineId  Machine id.
 * @param r          Governor droop R [pu].
 * @param t1         Governor time constant T1 [s].
 * @param t2         Compressor discharge volume time constant T2 [s].
 * @param t3         Combustor-turbine temperature time constant T3 [s].
 * @param at         Ambient temperature load limit AT [pu].
 * @param kt         Temperature limiter gain KT [pu].
 * @param vMax       Maximum turbine power VMAX [pu].
 * @param vMin       Minimum turbine power VMIN [pu].
 * @param dTurb      Turbine damping coefficient Dturb [pu].
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public record GastRecord(
        int busNumber,
        String modelName,
        String machineId,

        double r,      // Governor droop           [pu]
        double t1,     // Governor TC              [s]
        double t2,     // Compressor discharge TC  [s]
        double t3,     // Temperature TC           [s]
        double at,     // Ambient temp load limit  [pu]
        double kt,     // Temperature limiter gain [pu]
        double vMax,   // Max turbine power        [pu]
        double vMin,   // Min turbine power        [pu]
        double dTurb   // Turbine damping          [pu]
) implements DyrRecord { }
