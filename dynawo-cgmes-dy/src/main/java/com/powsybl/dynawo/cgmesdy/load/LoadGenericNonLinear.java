/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.cgmesdy.load;
/** LoadGenericNonLinear – Generic non-linear load. CIM: LoadGenericNonLinear
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>
@author Gautier Bureau {@literal <gautier.bureau at rte-france.com>*/
public record LoadGenericNonLinear(
    String id, String energyConsumerId,
    String genericNonLinearLoadModelType,
    double bs, double bt, double ls, double lt, double pt, double qt, double tp, double tq
) { }
