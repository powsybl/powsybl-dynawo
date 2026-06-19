/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.hydro;
/** GovHydroWEH – Woodward Electronic hydro governor. CIM: GovHydroWEH
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record GovHydroWEH(
    String id, String synchronousMachineId,
    double mwbase, double rpg, double rpp, double reg, double tg,
    double tp, double td, double tf, double td2, double tw,
    double pmax, double pmin, double gtmxop, double gtmxcl,
    double pmss1, double pmss2, double pmss3, double pmss4, double pmss5,
    double pmss6, double pmss7, double pmss8, double pmss9, double pmss10,
    double gpmax, double gmax, double gmin,
    double dpv, double dicn, double dspv, double feedbackSignal
) { }
