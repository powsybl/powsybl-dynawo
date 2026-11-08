/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.steam;
/**
 * GovSteam0 – Simple steam governor (basic single-reheat).
 * CIM: IEC 61970-302 GovSteam0
 * @param id                    RDF resource ID
 * @param synchronousMachineId  Associated SynchronousMachineDynamics ID
 * @param mwbase                Base for power values (MW)
 * @param r                     Permanent droop (PU)
 * @param t1                    Steam bowl time constant (s)
 * @param vmax                  Maximum valve position, PU of mwbase
 * @param vmin                  Minimum valve position, PU of mwbase
 * @param t2                    Numerator time constant of T2/T3 block (s)
 * @param t3                    Reheater time constant (s)
 * @param dt                    Turbine damping coefficient (PU)
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovSteam0(
    String id, String synchronousMachineId,
    double mwbase, double r, double t1,
    double vmax, double vmin, double t2, double t3, double dt
) { }
