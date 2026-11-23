/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.wind;
/** WindProtectionIEC – Grid protection model. CIM: WindProtectionIEC
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record WindProtectionIEC(
    String id,
    double dfimax, double fover, double funder, double mzc,
    double tfma, double uover, double uunder
) { }
