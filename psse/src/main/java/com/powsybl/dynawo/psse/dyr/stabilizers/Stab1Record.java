package com.powsybl.dynawo.psse.dyr.stabilizers;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>STAB1</b> – Single-Input Power System Stabiliser.
 *
 * <p>A classical single-input (speed or power) PSS with a washout filter and two
 * lead-lag compensation stages. One of the simplest PSS representations in PSS/E.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'STAB1' ID  KQ  TQ  T1  T2  T3  T4  HLim  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "STAB1"}.
 * @param machineId  Machine id.
 * @param kq         Stabiliser gain KQ [pu].
 * @param tq         Washout time constant TQ [s].
 * @param t1         Lead time constant of first stage T1 [s].
 * @param t2         Lag time constant of first stage T2 [s].
 * @param t3         Lead time constant of second stage T3 [s].
 * @param t4         Lag time constant of second stage T4 [s].
 * @param hLim       Output limiter HLim [pu].
 */
public record Stab1Record(
        int busNumber,
        String modelName,
        String machineId,

        double kq,     // Stabiliser gain         [pu]
        double tq,     // Washout TC              [s]
        double t1,     // Lead-lag 1 numerator TC [s]
        double t2,     // Lead-lag 1 denominator TC [s]
        double t3,     // Lead-lag 2 numerator TC [s]
        double t4,     // Lead-lag 2 denominator TC [s]
        double hLim    // Output hard limit       [pu]
) implements DyrRecord { }
