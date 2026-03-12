package com.powsybl.dynawo.psse.dyr.exciters;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>ESAC1A</b> – IEEE Alternator-Rectifier Excitation System Type AC1A (IEEE 421.5-2005).
 *
 * <p>Models a brushless excitation system using an AC alternator and uncontrolled rectifiers.
 * Includes demagnetisation effects (Kd), reactive current compensation, and detailed saturation.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'ESAC1A' ID  TR  TB  TC  KA  TA  VAMAX  VAMIN  TE  KF  TF
 *                       KL  VLMAX  KCS  KD  SEEFD1  SEEFD2  VFE1  VFE2  VRMAX  VRMIN  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "ESAC1A"}.
 * @param machineId  Machine id.
 * @param tr         Measurement transducer time constant TR [s].
 * @param tb         Regulator lead time constant TB [s].
 * @param tc         Regulator lag time constant TC [s].
 * @param ka         Voltage regulator gain KA [pu].
 * @param ta         Voltage regulator time constant TA [s].
 * @param vaMax      Regulator output max VAMAX [pu].
 * @param vaMin      Regulator output min VAMIN [pu].
 * @param te         Exciter time constant TE [s].
 * @param kf         Rate feedback gain KF [pu].
 * @param tf         Rate feedback time constant TF [s].
 * @param kl         Exciter field current limiter gain KL [pu].
 * @param vlMax      Exciter field voltage max VLMAX [pu].
 * @param kcs        Current source coefficient KCS [pu].
 * @param kd         Demagnetisation factor KD [pu].
 * @param seefd1     Saturation function value SE(E1) at first saturation point.
 * @param seefd2     Saturation function value SE(E2) at second saturation point.
 * @param vfe1       Exciter voltage at saturation point 1 VFE1 [pu].
 * @param vfe2       Exciter voltage at saturation point 2 VFE2 [pu].
 * @param vrMax      Maximum exciter field voltage VRMAX [pu].
 * @param vrMin      Minimum exciter field voltage VRMIN [pu].
 */
public record Esac1aRecord(
        int busNumber,
        String modelName,
        String machineId,

        // --- Measurement ---
        double tr,

        // --- Regulator lead-lag ---
        double tb,
        double tc,
        double ka,
        double ta,
        double vaMax,
        double vaMin,

        // --- Exciter ---
        double te,

        // --- Rate feedback ---
        double kf,
        double tf,

        // --- Field current limiter ---
        double kl,
        double vlMax,

        // --- AC rectifier coefficients ---
        double kcs,
        double kd,

        // --- Saturation ---
        double seefd1,
        double seefd2,
        double vfe1,
        double vfe2,

        // --- Output limits ---
        double vrMax,
        double vrMin
) implements DyrRecord { }
