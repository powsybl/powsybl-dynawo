package com.powsybl.dynawo.psse.dyr.stabilizers;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>PSS2B</b> – IEEE Dual-Input Power System Stabiliser Type 2B (IEEE 421.5-2005).
 *
 * <p>Extension of PSS2A with additional washout filters (Tw1–Tw4) on each input path,
 * providing better attenuation of torsional components and greater flexibility in
 * tuning for both inter-area and local modes simultaneously.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'PSS2B' ID  Input1  Input2  M  N
 *                      T1  T2  T3  T4  T5  T6  T7  T8
 *                      Ks1  Ks2  Ks3  T9  T10
 *                      Tw1  Tw2  Tw3  Tw4
 *                      LSMAX  LSMIN  VCU  VCL  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "PSS2B"}.
 * @param machineId  Machine id.
 * @param input1     First input signal selector.
 * @param input2     Second input signal selector.
 * @param m          Ramp-tracking filter numerator order.
 * @param n          Ramp-tracking filter denominator order.
 * @param t1         T1 [s].
 * @param t2         T2 [s].
 * @param t3         T3 [s].
 * @param t4         T4 [s].
 * @param t5         T5 [s].
 * @param t6         T6 [s].
 * @param t7         T7 [s].
 * @param t8         T8 [s].
 * @param ks1        Overall gain Ks1 [pu].
 * @param ks2        Signal 2 gain Ks2 [pu].
 * @param ks3        Output gain Ks3 [pu].
 * @param t9         T9 [s].
 * @param t10        T10 [s].
 * @param tw1        Washout 1 on path 1 Tw1 [s].
 * @param tw2        Washout 2 on path 1 Tw2 [s].
 * @param tw3        Washout 1 on path 2 Tw3 [s].
 * @param tw4        Washout 2 on path 2 Tw4 [s].
 * @param lsMax      Maximum output LSMAX [pu].
 * @param lsMin      Minimum output LSMIN [pu].
 * @param vcu        Over-voltage cutoff VCU [pu].
 * @param vcl        Under-voltage cutoff VCL [pu].
 */
public record Pss2bRecord(
        int busNumber,
        String modelName,
        String machineId,

        int input1,
        int input2,
        int m,
        int n,

        double t1,
        double t2,
        double t3,
        double t4,
        double t5,
        double t6,
        double t7,
        double t8,

        double ks1,
        double ks2,
        double ks3,

        double t9,
        double t10,

        // --- Additional washout filters (vs PSS2A) ---
        double tw1,
        double tw2,
        double tw3,
        double tw4,

        double lsMax,
        double lsMin,
        double vcu,
        double vcl
) implements DyrRecord { }
