package com.powsybl.dynawo.psse.dyr.generators;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>GENTP</b> – Salient-Pole Synchronous Generator model.
 *
 * <p>Salient-pole machines (hydro generators, synchronous condensers) have no q-axis
 * damper winding, so only one q-axis rotor circuit exists. The model uses the
 * sub-transient saliency approximation (X''d ≠ X''q).
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'GENTP' ID  T'do  T''do  T''qo  H  D
 *                      Xd   Xq   X'd   X''d  X''q  Xl  S(1.0)  S(1.2)  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "GENTP"}.
 * @param machineId  Machine id.
 * @param tpdo       d-axis transient open-circuit time constant T'do [s].
 * @param tppdo      d-axis sub-transient open-circuit time constant T''do [s].
 * @param tppqo      q-axis sub-transient open-circuit time constant T''qo [s].
 * @param h          Inertia constant [MW·s/MVA].
 * @param d          Damping coefficient [pu].
 * @param xd         d-axis synchronous reactance Xd [pu].
 * @param xq         q-axis synchronous reactance Xq [pu].
 * @param xpd        d-axis transient reactance X'd [pu].
 * @param xppd       d-axis sub-transient reactance X''d [pu].
 * @param xppq       q-axis sub-transient reactance X''q [pu].
 * @param xl         Leakage reactance Xl [pu].
 * @param s10        Saturation factor S(1.0).
 * @param s12        Saturation factor S(1.2).
 */
public record GentpRecord(
        int busNumber,
        String modelName,
        String machineId,

        // --- Time constants ---
        double tpdo,   // T'do  [s]
        double tppdo,  // T''do [s]
        double tppqo,  // T''qo [s]  (no T'qo for salient-pole)

        // --- Mechanical ---
        double h,      // Inertia constant     [MW·s/MVA]
        double d,      // Damping coefficient  [pu]

        // --- Reactances ---
        double xd,     // Synchronous d-axis   [pu]
        double xq,     // Synchronous q-axis   [pu]
        double xpd,    // Transient d-axis     [pu]
        double xppd,   // Sub-transient d-axis [pu]
        double xppq,   // Sub-transient q-axis [pu]  (X''q ≠ X''d for salient-pole)
        double xl,     // Leakage              [pu]

        // --- Saturation ---
        double s10,    // S(1.0)
        double s12     // S(1.2)
) implements DyrRecord { }
