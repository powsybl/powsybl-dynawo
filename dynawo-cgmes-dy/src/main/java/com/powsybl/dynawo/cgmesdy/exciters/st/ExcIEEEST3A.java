/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.st;
/** ExcIEEEST3A – IEEE Type ST3A compound-source rectifier with SCR. CIM: ExcIEEEST3A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record ExcIEEEST3A(
    String id, String synchronousMachineId,
    double tr, double vimax, double vimin,
    double ka, double ta, double tb, double tc,
    double vrmax, double vrmin, double km, double tm,
    double vmmax, double vmmin, double kg, double kp, double ki, double kc,
    double xl, double thetap, double vbmax, double vgmax,
    boolean uelin
) { }
