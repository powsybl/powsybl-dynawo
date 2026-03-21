package com.powsybl.dynawo.psse.dyr.renewables;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>REGCA1</b> – Renewable Energy Generator/Converter model type A (WECC 2nd gen).
 *
 * <p>Represents the converter interface (inverter) of a grid-connected renewable energy
 * source (solar PV, wind, battery storage). Models low-voltage power logic (LVPL),
 * high-voltage reactive current management, and current injection ramp limits.
 * Typically paired with REECA1 (electrical control) and a plant controller.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'REGCA1' ID  Tg  Rrpwr  Brkpt  Zerox  Lvpl1  Vo  Lv1  Vo1
 *                       Lv2  Vo2  Tfltr  Khv  Iqrmax  Iqrmin  Accel  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "REGCA1"}.
 * @param machineId  Machine id.
 * @param tg         Converter current control time constant Tg [s].
 * @param rrpwr      Active power recovery ramp rate Rrpwr [pu/s].
 * @param brkpt      Low-voltage power logic breakpoint voltage Brkpt [pu].
 * @param zerox      Low-voltage power logic zero crossing voltage Zerox [pu].
 * @param lvpl1      LVPL power limit at Brkpt Lvpl1 [pu].
 * @param vo         Voltage at which high-voltage current management activates Vo [pu].
 * @param lv1        LVPL ramp limit 1 Lv1 [pu/s].
 * @param vo1        Voltage for ramp limit 1 Vo1 [pu].
 * @param lv2        LVPL ramp limit 2 Lv2 [pu/s].
 * @param vo2        Voltage for ramp limit 2 Vo2 [pu].
 * @param tFltr      Reactive power filter time constant Tfltr [s].
 * @param khv        High-voltage reactive current gain Khv [pu].
 * @param iqrMax     Maximum reactive current injection ramp rate Iqrmax [pu/s].
 * @param iqrMin     Minimum reactive current injection ramp rate Iqrmin [pu/s].
 * @param accel      Acceleration factor for Q-control loop Accel.
 */
public record Regca1Record(
        int busNumber,
        String modelName,
        String machineId,

        // --- Converter dynamics ---
        double tg,       // Current control TC          [s]
        double rrpwr,    // Active power ramp rate      [pu/s]

        // --- Low-Voltage Power Logic (LVPL) ---
        double brkpt,    // Breakpoint voltage          [pu]
        double zerox,    // Zero-crossing voltage       [pu]
        double lvpl1,    // Power limit at breakpoint   [pu]

        // --- High-voltage reactive management ---
        double vo,
        double lv1,
        double vo1,
        double lv2,
        double vo2,

        // --- Reactive current filter & limits ---
        double tFltr,    // Reactive power filter TC    [s]
        double khv,      // HV reactive gain            [pu]
        double iqrMax,   // Iq ramp rate upper limit    [pu/s]
        double iqrMin,   // Iq ramp rate lower limit    [pu/s]

        // --- Control ---
        double accel     // Acceleration factor
) implements DyrRecord { }
