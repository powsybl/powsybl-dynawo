/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcAC4A – Non-IEEE AC4A alternator-rectifier variant. CIM: ExcAC4A
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcAC4A(
    String id, String synchronousMachineId,
    double tr, double ka, double ta, double tb, double tc,
    double vimax, double vimin, double vrmax, double vrmin, double kc
) { }
