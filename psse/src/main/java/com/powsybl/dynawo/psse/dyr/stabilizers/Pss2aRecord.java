package com.powsybl.dynawo.psse.dyr.stabilizers;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>PSS2A</b> – IEEE Dual-Input Power System Stabiliser Type 2A (IEEE 421.5-2005).
 *
 * <p>Combines two input signals (e.g. shaft speed and electrical power) to provide
 * superior damping over a wider frequency range than single-input stabilisers.
 * Features ramp-tracking filters and two lead-lag blocks per input path.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'PSS2A' ID  Input1  Input2  M  N
 *                      T1  T2  T3  T4  T5  T6  T7  T8
 *                      Ks1  Ks2  Ks3  T9  T10  LSMAX  LSMIN  VCU  VCL  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "PSS2A"}.
 * @param machineId  Machine id.
 * @param input1     First input signal selector (1=speed, 2=frequency, 3=power, 4=current).
 * @param input2     Second input signal selector.
 * @param m          Numerator order of ramp-tracking filter (integer).
 * @param n          Denominator order of ramp-tracking filter (integer).
 * @param t1         Ramp-tracking filter T1 [s].
 * @param t2         Ramp-tracking filter T2 [s].
 * @param t3         Lead-lag block T3 [s].
 * @param t4         Lead-lag block T4 [s].
 * @param t5         Lead-lag block T5 [s].
 * @param t6         Lead-lag block T6 [s].
 * @param t7         Second input washout T7 [s].
 * @param t8         Second input filter T8 [s].
 * @param ks1        Overall PSS gain Ks1 [pu].
 * @param ks2        Signal 2 gain Ks2 [pu].
 * @param ks3        Signal 3 (output) gain Ks3 [pu].
 * @param t9         Second input transducer T9 [s].
 * @param t10        Second input lead T10 [s].
 * @param lsMax      Maximum PSS output LSMAX [pu].
 * @param lsMin      Minimum PSS output LSMIN [pu].
 * @param vcu        Over-voltage cutoff VCU [pu] (PSS disabled above this terminal voltage).
 * @param vcl        Under-voltage cutoff VCL [pu] (PSS disabled below this terminal voltage).
 */
public record Pss2aRecord(
        int busNumber,
        String modelName,
        String machineId,

        // --- Input configuration ---
        int input1,
        int input2,
        int m,      // Ramp-tracking filter numerator order
        int n,      // Ramp-tracking filter denominator order

        // --- Time constants ---
        double t1,
        double t2,
        double t3,
        double t4,
        double t5,
        double t6,
        double t7,
        double t8,

        // --- Gains ---
        double ks1,
        double ks2,
        double ks3,

        // --- Additional time constants ---
        double t9,
        double t10,

        // --- Limits and cutoff voltages ---
        double lsMax,
        double lsMin,
        double vcu,
        double vcl
) implements DyrRecord { }
