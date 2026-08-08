/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.psse.dyr.facts;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>STATCON</b> – Static Synchronous Compensator (STATCOM) model.
 *
 * <p>Represents a VSC-based shunt reactive compensator (STATCOM) with inner current
 * control and outer voltage/reactive power regulation. Unlike a classical SVC, a
 * STATCOM can maintain full reactive current even at very low terminal voltages.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'STATCON' ID  Tr  Kp  Ki  Iqmax  Iqmin  Emax  Emin
 *                        Kf  Tf  Ks  Ts  T1  T2  Vref  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "STATCON"}.
 * @param machineId  Device id.
 * @param tr         Voltage measurement time constant Tr [s].
 * @param kp         Voltage regulator proportional gain Kp [pu].
 * @param ki         Voltage regulator integral gain Ki [pu/s].
 * @param iqMax      Maximum reactive current Iqmax [pu].
 * @param iqMin      Minimum reactive current Iqmin [pu].
 * @param eMax       Maximum internal VSC voltage Emax [pu].
 * @param eMin       Minimum internal VSC voltage Emin [pu].
 * @param kf         Rate feedback gain Kf [pu].
 * @param tf         Rate feedback time constant Tf [s].
 * @param ks         Current controller gain Ks [pu].
 * @param ts         Current controller time constant Ts [s].
 * @param t1         Lead time constant T1 [s].
 * @param t2         Lag time constant T2 [s].
 * @param vRef       Voltage reference (0 = use initial terminal voltage) Vref [pu].
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public record StatconRecord(
        int busNumber,
        String modelName,
        String machineId,

        // --- Measurement ---
        double tr,

        // --- Voltage regulator ---
        double kp,
        double ki,

        // --- Current limits ---
        double iqMax,
        double iqMin,

        // --- Internal voltage limits ---
        double eMax,
        double eMin,

        // --- Rate feedback stabiliser ---
        double kf,
        double tf,

        // --- Inner current controller ---
        double ks,
        double ts,

        // --- Lead-lag compensator ---
        double t1,
        double t2,

        // --- Reference ---
        double vRef
) implements DyrRecord { }
