package com.powsybl.dynawo.psse.dyr.renewables;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>REECA1</b> – Renewable Energy Electrical Control model type A (WECC 2nd gen).
 *
 * <p>Provides the electrical control (active and reactive power regulation) for
 * grid-connected converters. Interfaces with REGCA1 (converter) and REPC (plant
 * controller). Includes voltage/reactive power control modes, active current
 * priority, and fault current injection logic.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'REECA1' ID  PFFLAG  VFLAG  QFLAG  Vdip  Vup  Trv
 *                       dbd1  dbd2  Kqv  Iqh1  Iql1  Vref0  Iqfrz
 *                       Thld  Thld2  Tp  QMax  QMin  VMAX  VMIN
 *                       Kqp  Kqi  Kvp  Kvi  Vbias  Tiq
 *                       dPmax  dPmin  PMAX  PMIN  Imax  Tpord
 *                       Vq1  Iq1  Vq2  Iq2  Vq3  Iq3  Vq4  Iq4
 *                       Vp1  Ip1  Vp2  Ip2  Vp3  Ip3  Vp4  Ip4  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "REECA1"}.
 * @param machineId  Machine id.
 * @param pfFlag     Power factor control flag (0=Q-ctrl, 1=PF-ctrl).
 * @param vFlag      Voltage/reactive power control flag.
 * @param qFlag      Reactive power control flag.
 * @param vdip       Low-voltage LVRT threshold Vdip [pu].
 * @param vup        High-voltage HVRT threshold Vup [pu].
 * @param trv        Voltage transducer time constant Trv [s].
 * @param dbd1       Lower deadband for voltage error dbd1 [pu].
 * @param dbd2       Upper deadband for voltage error dbd2 [pu].
 * @param kqv        Reactive current injection gain during LVRT Kqv [pu].
 * @param iqh1       Maximum reactive current injection Iqh1 [pu].
 * @param iql1       Minimum reactive current injection Iql1 [pu].
 * @param vref0      Reference voltage (0 = use initial Vt) Vref0 [pu].
 * @param iqFrz      Frozen reactive current during LVRT Iqfrz [pu].
 * @param thld       Hold time after LVRT recovery Thld [s].
 * @param thld2      Second hold time Thld2 [s].
 * @param tp         Active power filter time constant Tp [s].
 * @param qMax       Maximum reactive power QMax [pu].
 * @param qMin       Minimum reactive power QMin [pu].
 * @param vMax       Maximum voltage reference VMAX [pu].
 * @param vMin       Minimum voltage reference VMIN [pu].
 * @param kqp        Q-controller proportional gain Kqp.
 * @param kqi        Q-controller integral gain Kqi.
 * @param kvp        Voltage-controller proportional gain Kvp.
 * @param kvi        Voltage-controller integral gain Kvi.
 * @param vBias      Voltage bias Vbias [pu].
 * @param tiq        Reactive current filter time constant Tiq [s].
 * @param dpMax      Maximum active power ramp rate dPmax [pu/s].
 * @param dpMin      Minimum active power ramp rate dPmin [pu/s].
 * @param pMax       Maximum active power PMAX [pu].
 * @param pMin       Minimum active power PMIN [pu].
 * @param iMax       Maximum total current Imax [pu].
 * @param tpOrd      Active power order filter TC Tpord [s].
 * @param vq1        Voltage for reactive current table point 1 [pu].
 * @param iq1        Reactive current for table point 1 [pu].
 * @param vq2        Voltage for reactive current table point 2 [pu].
 * @param iq2        Reactive current for table point 2 [pu].
 * @param vq3        Voltage for reactive current table point 3 [pu].
 * @param iq3        Reactive current for table point 3 [pu].
 * @param vq4        Voltage for reactive current table point 4 [pu].
 * @param iq4        Reactive current for table point 4 [pu].
 * @param vp1        Voltage for active current table point 1 [pu].
 * @param ip1        Active current for table point 1 [pu].
 * @param vp2        Voltage for active current table point 2 [pu].
 * @param ip2        Active current for table point 2 [pu].
 * @param vp3        Voltage for active current table point 3 [pu].
 * @param ip3        Active current for table point 3 [pu].
 * @param vp4        Voltage for active current table point 4 [pu].
 * @param ip4        Active current for table point 4 [pu].
 */
public record Reeca1Record(
        int busNumber,
        String modelName,
        String machineId,

        // --- Control mode flags ---
        int pfFlag,
        int vFlag,
        int qFlag,

        // --- LVRT / HVRT thresholds ---
        double vdip,
        double vup,

        // --- Voltage transducer ---
        double trv,

        // --- Deadbands ---
        double dbd1,
        double dbd2,

        // --- Reactive current injection ---
        double kqv,
        double iqh1,
        double iql1,
        double vref0,
        double iqFrz,

        // --- Hold times ---
        double thld,
        double thld2,

        // --- Active power filter ---
        double tp,

        // --- Q/V limits ---
        double qMax,
        double qMin,
        double vMax,
        double vMin,

        // --- Controller gains ---
        double kqp,
        double kqi,
        double kvp,
        double kvi,
        double vBias,
        double tiq,

        // --- Active power ramp and limits ---
        double dpMax,
        double dpMin,
        double pMax,
        double pMin,
        double iMax,
        double tpOrd,

        // --- Reactive current capability table (V, Iq) ---
        double vq1, double iq1,
        double vq2, double iq2,
        double vq3, double iq3,
        double vq4, double iq4,

        // --- Active current capability table (V, Ip) ---
        double vp1, double ip1,
        double vp2, double ip2,
        double vp3, double ip3,
        double vp4, double ip4
) implements DyrRecord { }
