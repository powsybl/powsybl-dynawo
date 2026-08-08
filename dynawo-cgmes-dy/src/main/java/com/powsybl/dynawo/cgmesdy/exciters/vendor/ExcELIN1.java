/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.exciters.vendor;
/** ExcELIN1 – ELIN exciter model 1. CIM: ExcELIN1
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
 */
public record ExcELIN1(
    String id, String synchronousMachineId,
    double tfi, double tnu, double ka, double ts1, double ts2,
    double dpnf, double vpu, double efmin, double efmax, double ks1, double ks2
) { }
