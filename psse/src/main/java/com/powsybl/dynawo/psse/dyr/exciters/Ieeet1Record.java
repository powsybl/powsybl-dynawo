package com.powsybl.dynawo.psse.dyr.exciters;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>IEEET1</b> – IEEE Type 1 Excitation System (1968 standard).
 *
 * <p>Represents a continuously acting rotating exciter with a voltage regulator.
 * Widely used for legacy thermal units. Includes a rate feedback stabilisation loop.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'IEEET1' ID  TR  KA  TA  TB  TC  VRMAX  VRMIN
 *                       KE  TE  KF  TF1  SWITCH  E1  SE1  E2  SE2  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "IEEET1"}.
 * @param machineId  Machine id.
 * @param tr         Measurement filter time constant TR [s].
 * @param ka         Regulator gain KA [pu].
 * @param ta         Regulator time constant TA [s].
 * @param tb         Regulator lead time constant TB [s].
 * @param tc         Regulator lag time constant TC [s].
 * @param vrMax      Maximum regulator output VRMAX [pu].
 * @param vrMin      Minimum regulator output VRMIN [pu].
 * @param ke         Exciter constant KE [pu].
 * @param te         Exciter time constant TE [s].
 * @param kf         Rate feedback gain KF [pu].
 * @param tf1        Rate feedback time constant TF1 [s].
 * @param switchFlag Exciter saturation model switch (0 = exponential, 1 = quadratic).
 * @param e1         Exciter output voltage for saturation point 1 E1 [pu].
 * @param se1        Saturation factor at E1, SE(E1).
 * @param e2         Exciter output voltage for saturation point 2 E2 [pu].
 * @param se2        Saturation factor at E2, SE(E2).
 */
public record Ieeet1Record(
        int busNumber,
        String modelName,
        String machineId,

        // --- Measurement ---
        double tr,         // Transducer time constant [s]

        // --- Voltage regulator ---
        double ka,         // Regulator gain           [pu]
        double ta,         // Regulator time constant  [s]
        double tb,         // Lead time constant       [s]
        double tc,         // Lag time constant        [s]
        double vrMax,      // Upper output limit       [pu]
        double vrMin,      // Lower output limit       [pu]

        // --- Exciter ---
        double ke,         // Exciter constant         [pu]
        double te,         // Exciter time constant    [s]

        // --- Rate feedback stabiliser ---
        double kf,         // Rate feedback gain       [pu]
        double tf1,        // Rate feedback time const [s]

        // --- Saturation ---
        int switchFlag, // Saturation model switch
        double e1,         // Voltage at saturation pt 1 [pu]
        double se1,        // SE(E1)
        double e2,         // Voltage at saturation pt 2 [pu]
        double se2         // SE(E2)
) implements DyrRecord { }
