package com.powsybl.dynawo.psse.dyr.generators;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>GENROU</b> – Detailed Round-Rotor Synchronous Generator model.
 *
 * <p>Implements the full 6th-order (two d-axis and two q-axis rotor windings) or
 * reduced-order round-rotor machine equations. This is the standard model for
 * large turbine-generators.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'GENROU' ID  T'do  T''do  T'qo  T''qo  H  D
 *                       Xd   Xq   X'd  X'q  X''d   Xl  S(1.0)  S(1.2)  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "GENROU"}.
 * @param machineId  Machine id.
 * @param tpdo       d-axis transient open-circuit time constant T'do [s].
 * @param tppdo      d-axis sub-transient open-circuit time constant T''do [s].
 * @param tpqo       q-axis transient open-circuit time constant T'qo [s].
 * @param tppqo      q-axis sub-transient open-circuit time constant T''qo [s].
 * @param h          Inertia constant [MW·s/MVA].
 * @param d          Damping coefficient [pu].
 * @param xd         d-axis synchronous reactance Xd [pu].
 * @param xq         q-axis synchronous reactance Xq [pu].
 * @param xpd        d-axis transient reactance X'd [pu].
 * @param xpq        q-axis transient reactance X'q [pu].
 * @param xppd       d-axis sub-transient reactance X''d [pu].  (X''q = X''d for round-rotor)
 * @param xl         Leakage reactance Xl [pu].
 * @param s10        Saturation factor at 1.0 pu flux S(1.0).
 * @param s12        Saturation factor at 1.2 pu flux S(1.2).
 */
public record GenrouRecord(
        int busNumber,
        String modelName,
        String machineId,

        // --- Time constants ---
        double tpdo,   // T'do  [s]
        double tppdo,  // T''do [s]
        double tpqo,   // T'qo  [s]
        double tppqo,  // T''qo [s]

        // --- Mechanical ---
        double h,      // Inertia constant     [MW·s/MVA]
        double d,      // Damping coefficient  [pu]

        // --- Reactances ---
        double xd,     // Synchronous d-axis   [pu]
        double xq,     // Synchronous q-axis   [pu]
        double xpd,    // Transient d-axis     [pu]
        double xpq,    // Transient q-axis     [pu]
        double xppd,   // Sub-transient d-axis [pu]
        double xl,     // Leakage              [pu]

        // --- Saturation ---
        double s10,    // S(1.0)
        double s12     // S(1.2)
) implements DyrRecord { }
