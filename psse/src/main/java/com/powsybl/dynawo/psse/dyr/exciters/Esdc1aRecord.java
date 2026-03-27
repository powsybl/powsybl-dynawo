package com.powsybl.dynawo.psse.dyr.exciters;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>ESDC1A</b> – IEEE DC Excitation System Type DC1A (IEEE 421.5-2005).
 *
 * <p>Represents a separately excited DC commutator exciter. Equivalent to the older IEEET1
 * but formulated according to the IEEE 421.5-2005 standard with consistent parameter naming.
 * Includes a lead-lag block in the regulator path (TC/TB) and rate feedback stabilisation.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'ESDC1A' ID  TR  KA  TA  TB  TC  VRMAX  VRMIN
 *                       KE  TE  KF  TF1  SWITCH  E1  SE1  E2  SE2  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "ESDC1A"}.
 * @param machineId  Machine id.
 * @param tr         Transducer time constant TR [s].
 * @param ka         Regulator gain KA [pu].
 * @param ta         Regulator time constant TA [s].
 * @param tb         Lead time constant TB [s].
 * @param tc         Lag time constant TC [s].
 * @param vrMax      Maximum regulator output VRMAX [pu].
 * @param vrMin      Minimum regulator output VRMIN [pu].
 * @param ke         Exciter constant related to self-excited field KE [pu].
 * @param te         Exciter time constant TE [s].
 * @param kf         Rate feedback gain KF [pu].
 * @param tf1        Rate feedback time constant TF1 [s].
 * @param switchFlag Saturation model switch (0 = exponential, 1 = quadratic).
 * @param e1         Voltage at saturation point 1 E1 [pu].
 * @param se1        Saturation factor SE(E1).
 * @param e2         Voltage at saturation point 2 E2 [pu].
 * @param se2        Saturation factor SE(E2).
 */
public record Esdc1aRecord(
        int busNumber,
        String modelName,
        String machineId,

        double tr,
        double ka,
        double ta,
        double tb,
        double tc,
        double vrMax,
        double vrMin,
        double ke,
        double te,
        double kf,
        double tf1,
        int switchFlag,
        double e1,
        double se1,
        double e2,
        double se2
) implements DyrRecord { }
