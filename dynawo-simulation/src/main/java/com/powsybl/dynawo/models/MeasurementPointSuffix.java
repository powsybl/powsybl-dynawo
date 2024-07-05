/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models;

import com.powsybl.dynawo.models.macroconnections.MacroConnectionSuffix;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class MeasurementPointSuffix implements MacroConnectionSuffix {

    private static final String MEASURE_PREFIX = "Measure";

    private final String idSuffix;
    private final String connectionSuffix;

    private MeasurementPointSuffix(String idSuffix, String connectionSuffix) {
        this.idSuffix = idSuffix;
        this.connectionSuffix = connectionSuffix;
    }

    public static MeasurementPointSuffix of(int i) {
        if (i < 1) {
            throw new IllegalArgumentException();
        }
        return new MeasurementPointSuffix(MEASURE_PREFIX + i, i != 1 ? String.valueOf(i) : "");
    }

    public String getIdSuffix() {
        return idSuffix;
    }

    public String getConnectionSuffix() {
        return connectionSuffix;
    }
}
