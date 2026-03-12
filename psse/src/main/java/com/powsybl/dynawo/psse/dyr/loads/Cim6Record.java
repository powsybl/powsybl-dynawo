package com.powsybl.dynawo.psse.dyr.loads;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>CIM6</b> – Composite Induction Motor Load Model (6th-order, Double-Cage).
 *
 * <p>Extends CIM5 by adding a second rotor cage (R2, X2, T2o), enabling more accurate
 * representation of deep-bar and double-cage motors used in large compressors and pumps.
 * The additional cage primarily affects the starting and short-circuit transient behaviour.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'CIM6' ID  Ra  Xa  Xm  R1  X1  R2  X2  T1o  T2o  LSS  H
 *                     Etrip  Ttrip  Pfrac  Tb  D  Vc1  Vd1  Vc2  Vd2
 *                     Vbrk  Frst  Vrst  Tst  Vst  VCA  VCB  EF  LF  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "CIM6"}.
 * @param machineId  Load id.
 * @param ra         Stator resistance Ra [pu].
 * @param xa         Stator leakage reactance Xa [pu].
 * @param xm         Magnetising reactance Xm [pu].
 * @param r1         First cage rotor resistance R1 [pu].
 * @param x1         First cage rotor leakage reactance X1 [pu].
 * @param r2         Second cage rotor resistance R2 [pu].
 * @param x2         Second cage rotor leakage reactance X2 [pu].
 * @param t1o        First cage open-circuit time constant T1o [s].
 * @param t2o        Second cage open-circuit time constant T2o [s].
 * @param lss        Synchronous reactance Lss [pu].
 * @param h          Motor inertia H [MW·s/MVA].
 * @param eTrip      Trip voltage threshold Etrip [pu].
 * @param tTrip      Trip delay Ttrip [s].
 * @param pFrac      Motor fraction of total load Pfrac.
 * @param tb         Mechanical load torque breakpoint Tb [pu].
 * @param d          Mechanical damping D [pu].
 * @param vc1        Contactor close voltage Vc1 [pu].
 * @param vd1        Contactor open voltage Vd1 [pu].
 * @param vc2        Second contactor close voltage Vc2 [pu].
 * @param vd2        Second contactor open voltage Vd2 [pu].
 * @param vBrk       Stall break voltage Vbrk [pu].
 * @param fRst       Restart frequency Frst [Hz].
 * @param vRst       Restart voltage Vrst [pu].
 * @param tSt        Stall time constant Tst [s].
 * @param vSt        Stall voltage threshold Vst [pu].
 * @param vca        Compressor A cut-off voltage VCA [pu].
 * @param vcb        Compressor B cut-off voltage VCB [pu].
 * @param ef         Compressor efficiency EF.
 * @param lf         Load fraction factor LF.
 */
public record Cim6Record(
        int busNumber,
        String modelName,
        String machineId,

        // --- Electrical circuit (double-cage) ---
        double ra,
        double xa,
        double xm,
        double r1,
        double x1,
        double r2,   // ← second cage (vs CIM5)
        double x2,   // ← second cage
        double t1o,
        double t2o,  // ← second cage TC
        double lss,

        // --- Mechanical ---
        double h,
        double eTrip,
        double tTrip,
        double pFrac,
        double tb,
        double d,

        // --- Contactor logic ---
        double vc1,
        double vd1,
        double vc2,
        double vd2,

        // --- Stall and restart ---
        double vBrk,
        double fRst,
        double vRst,
        double tSt,
        double vSt,

        // --- Thermal / compressor cut-off ---
        double vca,
        double vcb,
        double ef,
        double lf
) implements DyrRecord { }
