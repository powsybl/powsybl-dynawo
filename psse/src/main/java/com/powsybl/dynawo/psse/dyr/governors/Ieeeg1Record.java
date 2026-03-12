package com.powsybl.dynawo.psse.dyr.governors;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>IEEEG1</b> – IEEE Multi-Stage Steam Turbine-Governor Model.
 *
 * <p>Represents a multi-stage (HP/LP) tandem-compound or cross-compound steam turbine
 * with speed governing. Splits power output between up to four turbine stages using
 * fraction coefficients K1–K8.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'IEEEG1' ID  K  T1  T2  T3  Uo  Uc  PMAX  PMIN
 *                       T4  K1  K2  T5  K3  K4  T6  K5  K6  T7  K7  K8  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "IEEEG1"}.
 * @param machineId  Machine id.
 * @param k          Governor gain (inverse of droop) K [pu].
 * @param t1         Governor lead time constant T1 [s].
 * @param t2         Governor lag time constant T2 [s].
 * @param t3         Valve positioner time constant T3 [s].
 * @param uo         Maximum gate opening rate Uo [pu/s].
 * @param uc         Maximum gate closing rate Uc [pu/s].
 * @param pMax       Maximum turbine power PMAX [pu].
 * @param pMin       Minimum turbine power PMIN [pu].
 * @param t4         HP turbine time constant T4 [s].
 * @param k1         HP turbine power fraction K1.
 * @param k2         LP1 reheater fraction for HP stage K2.
 * @param t5         LP turbine / reheater time constant T5 [s].
 * @param k3         LP turbine power fraction K3.
 * @param k4         LP1 turbine fraction K4.
 * @param t6         LP second-stage time constant T6 [s].
 * @param k5         LP second-stage fraction K5.
 * @param k6         LP second-stage fraction K6.
 * @param t7         LP third-stage time constant T7 [s].
 * @param k7         LP third-stage fraction K7.
 * @param k8         LP third-stage fraction K8.
 */
public record Ieeeg1Record(
        int busNumber,
        String modelName,
        String machineId,

        // --- Governor block ---
        double k,      // Governor gain (1/droop)  [pu]
        double t1,     // Lead time constant       [s]
        double t2,     // Lag time constant        [s]
        double t3,     // Valve positioner TC      [s]
        double uo,     // Gate opening rate limit  [pu/s]
        double uc,     // Gate closing rate limit  [pu/s]
        double pMax,   // Max power output         [pu]
        double pMin,   // Min power output         [pu]

        // --- Turbine stages ---
        double t4,     // HP turbine TC            [s]
        double k1,     // HP fraction
        double k2,
        double t5,     // LP/reheater TC           [s]
        double k3,
        double k4,
        double t6,
        double k5,
        double k6,
        double t7,
        double k7,
        double k8
) implements DyrRecord { }
