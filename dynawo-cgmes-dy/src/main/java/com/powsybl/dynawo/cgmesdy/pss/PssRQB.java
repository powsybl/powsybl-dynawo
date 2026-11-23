/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.pss;
/** PssRQB – French RQB stabilizer. CIM: PssRQB
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record PssRQB(
    String id, String excitationSystemId,
    double kdpm, double ki2, double ki3, double ki4, double sibv,
    double t4f, double t4m, double t4mom, double tomd, double tomsl
) { }
