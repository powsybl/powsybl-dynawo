package com.powsybl.dynawo.psse.dyr.facts;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>CSVGN</b> – Static Var Compensator (SVC) model.
 *
 * <p>Represents a thyristor-controlled SVC with a first-order transfer function
 * controller and susceptance limits. The model captures the fundamental voltage
 * regulation behaviour of the SVC without modelling individual thyristor firing.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'CSVGN' ID  K  T1  T2  T3  T4  T5  CMAX  CMIN  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "CSVGN"}.
 * @param machineId  Device id.
 * @param k          Regulator gain K [pu].
 * @param t1         Measurement transducer time constant T1 [s].
 * @param t2         Controller lead time constant T2 [s].
 * @param t3         Controller lag time constant T3 [s].
 * @param t4         Gate firing delay time constant T4 [s].
 * @param t5         SVC firing system time constant T5 [s].
 * @param cMax       Maximum susceptance output CMAX [pu on SVC MVA base].
 * @param cMin       Minimum susceptance output CMIN [pu on SVC MVA base].
 */
public record CsvgnRecord(
        int busNumber,
        String modelName,
        String machineId,

        double k,      // Regulator gain              [pu]
        double t1,     // Measurement TC              [s]
        double t2,     // Lead time constant          [s]
        double t3,     // Lag time constant           [s]
        double t4,     // Gate firing delay TC        [s]
        double t5,     // Firing system TC            [s]
        double cMax,   // Max susceptance             [pu]
        double cMin    // Min susceptance             [pu]
) implements DyrRecord { }
