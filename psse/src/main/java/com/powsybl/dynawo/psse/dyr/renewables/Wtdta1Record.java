package com.powsybl.dynawo.psse.dyr.renewables;

import com.powsybl.dynawo.psse.dyr.DyrRecord;

/**
 * PSS/E <b>WTDTA1</b> – Wind Turbine Drive-Train model type A (WECC 2nd gen).
 *
 * <p>A two-mass shaft model representing the mechanical coupling between the wind
 * turbine rotor (blade/hub mass) and the generator rotor. Torsional oscillations
 * between these two masses can excite sub-synchronous resonance or interact with
 * generator controls.
 *
 * <p>dyr file column order:
 * <pre>
 *   BUSNUM 'WTDTA1' ID  H  DAMP  Hself  KSelf  Theta  /
 * </pre>
 *
 * @param busNumber  Bus number.
 * @param modelName  Always {@code "WTDTA1"}.
 * @param machineId  Machine id.
 * @param h          Generator rotor inertia constant H [MW·s/MVA].
 * @param damp       Shaft damping coefficient DAMP [pu torque / pu speed].
 * @param hSelf      Wind turbine rotor inertia constant Hself [MW·s/MVA].
 * @param kSelf      Shaft spring stiffness KSelf [pu torque / rad].
 * @param theta      Initial shaft twist angle Theta [rad].
 */
public record Wtdta1Record(
        int busNumber,
        String modelName,
        String machineId,

        double h,       // Generator rotor inertia  [MW·s/MVA]
        double damp,    // Shaft damping            [pu]
        double hSelf,   // Wind rotor inertia       [MW·s/MVA]
        double kSelf,   // Shaft stiffness          [pu/rad]
        double theta    // Initial shaft twist      [rad]
) implements DyrRecord { }
