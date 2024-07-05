/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public enum TransformerSide {
    HIGH_VOLTAGE("T"),
    LOW_VOLTAGE("D"),
    NONE("");

    private final String sideSuffix;

    TransformerSide(String sideSuffix) {
        this.sideSuffix = sideSuffix;
    }

    public String getSideSuffix() {
        return sideSuffix;
    }
}
