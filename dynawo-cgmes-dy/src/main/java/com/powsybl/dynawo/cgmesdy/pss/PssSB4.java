/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.pss;
/** PssSB4 – Smith-Bowler 4-input PSS. CIM: PssSB4
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record PssSB4(
    String id, String excitationSystemId,
    double tt, double kx, double tx1, double tx2, double tx3, double tx4,
    double vsmax, double vsmin
) { }
