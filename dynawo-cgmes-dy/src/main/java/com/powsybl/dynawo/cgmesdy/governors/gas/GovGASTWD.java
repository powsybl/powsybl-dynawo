/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.governors.gas;
/** GovGASTWD – Woodward gas turbine governor. CIM: GovGASTWD
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record GovGASTWD(
    String id, String synchronousMachineId,
    double mwbase, double r, double rdown, double rup,
    double ta, double tact, double tb, double tc, double tf,
    double kdroop, double etd, double tcd, double trate, double teng,
    double td, double tltr, double tsa, double tsb,
    double vmax, double vmin, double dpv, double kpgov, double kigov
) { }
