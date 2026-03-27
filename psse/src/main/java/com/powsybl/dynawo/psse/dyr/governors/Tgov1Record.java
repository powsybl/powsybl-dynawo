package com.powsybl.dynawo.psse.dyr.governors;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>TGOV1</b> – Simple Thermal Turbine-Governor Model.
 *
 * <p>A single-reheat turbine governor with a droop-based speed governor.
 * Suitable for steam turbines where only approximate governor response is needed.
 * This is one of the most commonly used governors in power system stability studies.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'TGOV1' ID  R  T1  VMAX  VMIN  T2  T3  Dt  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "TGOV1"}.
 * @param machineId  Machine id.
 * @param r          Permanent droop R [pu].
 * @param t1         Governor time constant T1 [s].
 * @param vMax       Maximum valve position VMAX [pu].
 * @param vMin       Minimum valve position VMIN [pu].
 * @param t2         Turbine lead time constant T2 [s].
 * @param t3         Turbine lag (reheater) time constant T3 [s].
 * @param dt         Turbine damping coefficient Dt [pu].
 */
public record Tgov1Record(
        int busNumber,
        String modelName,
        String machineId,

        double r,      // Permanent droop            [pu]
        double t1,     // Governor time constant     [s]
        double vMax,   // Max valve position         [pu]
        double vMin,   // Min valve position         [pu]
        double t2,     // Turbine lead time const    [s]
        double t3,     // Reheater time constant     [s]
        double dt      // Turbine damping            [pu]
) implements DyrRecord { }
