/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.pss;
/** Pss5 – Multi-input PSS. CIM: Pss5
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record Pss5(
    String id, String excitationSystemId,
    double deadband, double isfreq, double kf, double kpe, double kpss, double ktgov,
    double pmin, double tl1, double tl2, double tl3, double tl4, double tpe,
    double tw1, double tw2, double vadat, double vsmn, double vsmx
) { }
