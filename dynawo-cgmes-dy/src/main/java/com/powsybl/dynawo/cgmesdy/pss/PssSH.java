/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.pss;
/** PssSH – Simplified hydro PSS. CIM: PssSH
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record PssSH(
    String id, String excitationSystemId,
    double k, double k0, double k1, double k2, double k3, double k4,
    double t1, double t2, double t3, double t4, double td,
    double vsmax, double vsmin
) { }
