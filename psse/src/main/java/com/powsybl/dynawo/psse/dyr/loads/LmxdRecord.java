package com.powsybl.dynawo.psse.dyr.loads;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>LMXD</b> – Single-Cage Induction Motor Load Model.
 *
 * <p>Represents the dynamic behaviour of a lumped induction motor load connected to a bus.
 * Often used to represent industrial motor loads that are sensitive to voltage recovery
 * following faults. Includes rotor flux dynamics and mechanical load representation.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'LMXD' ID  Ra  Xs  Xr  Xm  Rr  Tr  Hm  D
 *                     Tpo  Tppo  Ls  Lp  Lpp  Vt  Tv  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "LMXD"}.
 * @param machineId  Load id.
 * @param ra         Stator resistance Ra [pu].
 * @param xs         Stator leakage reactance Xs [pu].
 * @param xr         Rotor leakage reactance Xr [pu].
 * @param xm         Magnetising reactance Xm [pu].
 * @param rr         Rotor resistance Rr [pu].
 * @param tr         Rotor time constant Tr [s].
 * @param hm         Motor inertia Hm [MW·s/MVA].
 * @param d          Mechanical damping D [pu].
 * @param tpo        Transient open-circuit time constant T'o [s].
 * @param tppo       Sub-transient open-circuit time constant T''o [s].
 * @param ls         Synchronous reactance Ls [pu].
 * @param lp         Transient reactance L' [pu].
 * @param lpp        Sub-transient reactance L'' [pu].
 * @param vt         Terminal voltage threshold for stalling Vt [pu].
 * @param tv         Stall voltage duration Tv [s].
 */
public record LmxdRecord(
        int busNumber,
        String modelName,
        String machineId,

        // --- Electrical parameters ---
        double ra,     // Stator resistance        [pu]
        double xs,     // Stator leakage reactance [pu]
        double xr,     // Rotor leakage reactance  [pu]
        double xm,     // Magnetising reactance    [pu]
        double rr,     // Rotor resistance         [pu]
        double tr,     // Rotor time constant      [s]

        // --- Mechanical ---
        double hm,     // Motor inertia            [MW·s/MVA]
        double d,      // Mechanical damping       [pu]

        // --- Reduced-order equivalents ---
        double tpo,    // Transient open-circuit TC    [s]
        double tppo,   // Sub-transient open-circuit TC [s]
        double ls,     // Synchronous reactance     [pu]
        double lp,     // Transient reactance       [pu]
        double lpp,    // Sub-transient reactance   [pu]

        // --- Stall logic ---
        double vt,     // Stall voltage threshold   [pu]
        double tv      // Stall time delay          [s]
) implements DyrRecord { }
