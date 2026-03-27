package com.powsybl.dynawo.psse.dyr.loads;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>CIM5</b> – Composite Induction Motor Load Model (5th-order).
 *
 * <p>A detailed single-cage induction motor model that represents the electromagnetic
 * transients in both d- and q-axis stator flux. Used when accurate representation of
 * motor voltage recovery after faults is required, especially for large industrial loads.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'CIM5' ID  Ra  Xa  Xm  R1  X1  T1o  LSS  H  Etrip  Ttrip
 *                     Pfrac  Tb  D  Vc1  Vd1  Vc2  Vd2  Vbrk  Frst  Vrst
 *                     Tst  Vst  VCA  VCB  EF  LF  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "CIM5"}.
 * @param machineId  Load id.
 * @param ra         Stator resistance Ra [pu].
 * @param xa         Stator leakage reactance Xa [pu].
 * @param xm         Magnetising reactance Xm [pu].
 * @param r1         Rotor resistance R1 [pu].
 * @param x1         Rotor leakage reactance X1 [pu].
 * @param t1o        Rotor open-circuit time constant T1o [s].
 * @param lss        Synchronous reactance Lss [pu].
 * @param h          Motor inertia H [MW·s/MVA].
 * @param eTrip      Trip voltage threshold Etrip [pu].
 * @param tTrip      Trip delay Ttrip [s].
 * @param pFrac      Motor fraction of total load power Pfrac.
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
 * @param vca        Compressor A thermal cutoff voltage VCA [pu].
 * @param vcb        Compressor B thermal cutoff voltage VCB [pu].
 * @param ef         Compressor efficiency EF.
 * @param lf         Load fraction factor LF.
 */
public record Cim5Record(
        int busNumber,
        String modelName,
        String machineId,

        // --- Electrical circuit ---
        double ra,
        double xa,
        double xm,
        double r1,
        double x1,
        double t1o,
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
