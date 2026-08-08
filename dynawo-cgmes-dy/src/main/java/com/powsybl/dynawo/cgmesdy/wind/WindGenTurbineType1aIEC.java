/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.wind;
/** WindGenTurbineType1aIEC – Fixed speed induction (stall regulated). CIM: WindGenTurbineType1aIEC
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record WindGenTurbineType1aIEC(
    String id, String powerPlantId,
    String windAeroConstIECId, String windProtectionIECId,
    String windMechIECId, String asynchronousMachineId
) { }
