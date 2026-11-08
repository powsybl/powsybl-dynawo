/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.protection;

/** VCompIEEEType1 – IEEE type 1 voltage compensator (line-drop compensation).
 *  CIM class: VCompIEEEType1 (IEC 61970-302 §5.7.1)
 *  @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record VCompIEEEType1(
        String id,
        String excitationSystemId,
        double tr,    // Filter time constant (s)
        double rc,  // Resistance compensation element (PU)
        double xc   // Reactance compensation element (PU)
) { }
