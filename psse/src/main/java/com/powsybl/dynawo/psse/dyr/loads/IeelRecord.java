package com.powsybl.dynawo.psse.dyr.loads;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>IEEL</b> – Static (Exponential/Polynomial) Load Model.
 *
 * <p>Represents a voltage-dependent static load using separate exponential characteristic
 * functions for the active (P) and reactive (Q) components. Two operating points are
 * specified so that a piecewise-linear saturation characteristic can be modelled.
 *
 * <p>The load power follows:
 * <pre>
 *   P = P0 · ( PF1·V^EX1  +  PF2·V^EX2 )
 *   Q = Q0 · ( QF1·V^EX3  +  QF2·V^EX4 )
 * </pre>
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'IEEL' ID  PF1  EX1  PF2  EX2  QF1  EX3  QF2  EX4  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "IEEL"}.
 * @param machineId  Load id.
 * @param pf1        Active power fraction in first characteristic PF1.
 * @param ex1        Voltage exponent for active power first term EX1.
 * @param pf2        Active power fraction in second characteristic PF2 (PF1 + PF2 = 1).
 * @param ex2        Voltage exponent for active power second term EX2.
 * @param qf1        Reactive power fraction in first characteristic QF1.
 * @param ex3        Voltage exponent for reactive power first term EX3.
 * @param qf2        Reactive power fraction in second characteristic QF2.
 * @param ex4        Voltage exponent for reactive power second term EX4.
 */
public record IeelRecord(
        int busNumber,
        String modelName,
        String machineId,

        // --- Active power characteristic ---
        double pf1,    // P fraction 1
        double ex1,    // P voltage exponent 1
        double pf2,    // P fraction 2
        double ex2,    // P voltage exponent 2

        // --- Reactive power characteristic ---
        double qf1,    // Q fraction 1
        double ex3,    // Q voltage exponent 1
        double qf2,    // Q fraction 2
        double ex4     // Q voltage exponent 2
) implements DyrRecord { }
