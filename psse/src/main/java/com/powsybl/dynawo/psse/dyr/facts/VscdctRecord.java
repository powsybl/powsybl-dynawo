package com.powsybl.dynawo.psse.dyr.facts;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>VSCDCT</b> – Voltage Source Converter (VSC) HVDC Terminal model.
 *
 * <p>Represents a single VSC terminal of a point-to-point or multi-terminal HVDC link.
 * Models the inner current controller, outer power/voltage controller, and DC capacitor
 * dynamics. Both the rectifier and inverter terminals are represented by separate
 * VSCDCT records sharing the same DC link id.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'VSCDCT' ID  Tr  Kp  Ki  Vdcmax  Vdcmin
 *                       Iqmax  Iqmin  Idmax  Idmin
 *                       Tp  Tq  Kpdc  Kidc  Cdc  /
 * </pre>
 *
 * @param busNumber  Bus number of the AC terminal.
 * @param modelName  Always {@code "VSCDCT"}.
 * @param machineId  Converter station id.
 * @param tr         AC voltage measurement time constant Tr [s].
 * @param kp         Outer voltage controller proportional gain Kp [pu].
 * @param ki         Outer voltage controller integral gain Ki [pu/s].
 * @param vdcMax     Maximum DC voltage reference Vdcmax [pu].
 * @param vdcMin     Minimum DC voltage reference Vdcmin [pu].
 * @param iqMax      Maximum reactive current injection Iqmax [pu].
 * @param iqMin      Minimum reactive current injection Iqmin [pu].
 * @param idMax      Maximum active current Idmax [pu].
 * @param idMin      Minimum active current Idmin [pu].
 * @param tp         Active power filter time constant Tp [s].
 * @param tq         Reactive power filter time constant Tq [s].
 * @param kpdc       DC voltage droop proportional gain Kpdc [pu].
 * @param kidc       DC voltage droop integral gain Kidc [pu/s].
 * @param cdc        DC capacitor size Cdc [pu on DC MVA base].
 */
public record VscdctRecord(
        int busNumber,
        String modelName,
        String machineId,

        // --- AC measurement ---
        double tr,

        // --- Outer controller ---
        double kp,
        double ki,
        double vdcMax,
        double vdcMin,

        // --- Current limits ---
        double iqMax,
        double iqMin,
        double idMax,
        double idMin,

        // --- Power filters ---
        double tp,
        double tq,

        // --- DC voltage droop ---
        double kpdc,
        double kidc,

        // --- DC capacitor ---
        double cdc
) implements DyrRecord { }
