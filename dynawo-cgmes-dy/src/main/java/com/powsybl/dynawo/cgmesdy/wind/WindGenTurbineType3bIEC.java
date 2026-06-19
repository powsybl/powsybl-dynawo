/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.wind;
/** WindGenTurbineType3bIEC – DFIG with mechanical model. CIM: WindGenTurbineType3bIEC
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public record WindGenTurbineType3bIEC(
    String id, String powerPlantId,
    String windContPType3IECId, String windContQIECId, String windMechIECId,
    String windContCurrLimIECId, String windProtectionIECId,
    double fthres, double mwtcwp, double tg, double two
) { }
