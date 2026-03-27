package com.powsybl.dynawo.psse.dyr.exciters;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>IEEEX1</b> – IEEE Type X1 Excitation System.
 *
 * <p>An alternative to IEEET1 with a simplified lead-lag compensator in the regulator path.
 * The model omits the separate transient gain reduction block found in some modern exciters.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'IEEEX1' ID  TR  KA  TA  TB  TC  VRMAX  VRMIN
 *                       KE  TE  KF  TF1  E1  SE1  E2  SE2  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "IEEEX1"}.
 * @param machineId  Machine id.
 * @param tr         Measurement filter time constant TR [s].
 * @param ka         Regulator gain KA [pu].
 * @param ta         Regulator time constant TA [s].
 * @param tb         Lead time constant TB [s].
 * @param tc         Lag time constant TC [s].
 * @param vrMax      Maximum regulator output VRMAX [pu].
 * @param vrMin      Minimum regulator output VRMIN [pu].
 * @param ke         Exciter constant KE [pu].
 * @param te         Exciter time constant TE [s].
 * @param kf         Rate feedback gain KF [pu].
 * @param tf1        Rate feedback time constant TF1 [s].
 * @param e1         Exciter voltage at saturation point 1 [pu].
 * @param se1        Saturation factor SE(E1).
 * @param e2         Exciter voltage at saturation point 2 [pu].
 * @param se2        Saturation factor SE(E2).
 */
public record Ieeex1Record(
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
        double e1,
        double se1,
        double e2,
        double se2
) implements DyrRecord { }
