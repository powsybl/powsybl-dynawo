/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.pss;
/** PssELIN2 – ELIN PSS model. CIM: PssELIN2
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record PssELIN2(
    String id, String excitationSystemId,
    double apss, double ks1, double ks2, double ppss, double psslim, double ts1, double ts2,
    double ts3, double ts4, double ts5, double ts6
) { }
