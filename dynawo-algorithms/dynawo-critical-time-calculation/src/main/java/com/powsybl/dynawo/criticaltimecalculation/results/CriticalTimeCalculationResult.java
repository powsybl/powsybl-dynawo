/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.results;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public record CriticalTimeCalculationResult(String id, Status status, double criticalTime) {
    public CriticalTimeCalculationResult(String id, Status status) {
        this(id, status, Double.NaN);
    }
}
