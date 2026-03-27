package com.powsybl.dynawo.psse.dyr.governors;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>HYGOV</b> – Hydraulic Turbine-Governor Model.
 *
 * <p>Represents a hydroelectric turbine with nonlinear turbine characteristics and a
 * PID speed governor. Includes penstock water-inertia effects (water starting time TW)
 * and velocity limits on the gate servomotor.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'HYGOV' ID  R  r  TR  TF  TG  VELM  GMAX  GMIN  TW  At  Dturb  qNL  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "HYGOV"}.
 * @param machineId  Machine id.
 * @param r          Permanent droop R [pu].
 * @param rTemp      Temporary droop r [pu].
 * @param tr         Dashpot reset time constant TR [s].
 * @param tf         Filter time constant TF [s].
 * @param tg         Gate servomotor time constant TG [s].
 * @param velM       Gate velocity limit VELM [pu/s].
 * @param gMax       Maximum gate opening GMAX [pu].
 * @param gMin       Minimum gate opening GMIN [pu].
 * @param tw         Water starting time constant TW [s].
 * @param at         Turbine gain At [pu].
 * @param dTurb      Turbine damping Dturb [pu].
 * @param qNl        No-load turbine flow qNL [pu].
 */
public record HygovRecord(
        int busNumber,
        String modelName,
        String machineId,

        double r,      // Permanent droop          [pu]
        double rTemp,  // Temporary droop          [pu]
        double tr,     // Dashpot reset TC         [s]
        double tf,     // Filter TC                [s]
        double tg,     // Gate servomotor TC       [s]
        double velM,   // Gate velocity limit      [pu/s]
        double gMax,   // Max gate opening         [pu]
        double gMin,   // Min gate opening         [pu]
        double tw,     // Water starting time      [s]
        double at,     // Turbine gain             [pu]
        double dTurb,  // Turbine damping          [pu]
        double qNl     // No-load flow             [pu]
) implements DyrRecord { }
