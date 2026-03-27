package com.powsybl.dynawo.psse.dyr.exciters;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>SEXS</b> – Simplified Excitation System.
 *
 * <p>A very compact two-parameter-block exciter model intended for use when detailed
 * exciter data is unavailable. A single lead-lag compensator and first-order regulator
 * are combined. Frequently used for equivalent machines in large-scale studies.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'SEXS' ID  TATB  TB  K  TE  EMIN  EMAX  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "SEXS"}.
 * @param machineId  Machine id.
 * @param taTb       Ratio TA/TB of lead-lag compensator (0 &lt; TA/TB ≤ 1).
 * @param tb         Lead-lag denominator time constant TB [s].
 * @param k          Regulator gain K [pu].
 * @param te         Exciter time constant TE [s].
 * @param eMin       Minimum exciter output voltage EMIN [pu].
 * @param eMax       Maximum exciter output voltage EMAX [pu].
 */
public record SexsRecord(
        int busNumber,
        String modelName,
        String machineId,

        double taTb,   // TA/TB ratio (lead-lag)
        double tb,     // Lead-lag denom time const [s]
        double k,      // Regulator gain            [pu]
        double te,     // Exciter time constant     [s]
        double eMin,   // Lower field voltage limit [pu]
        double eMax    // Upper field voltage limit [pu]
) implements DyrRecord { }
