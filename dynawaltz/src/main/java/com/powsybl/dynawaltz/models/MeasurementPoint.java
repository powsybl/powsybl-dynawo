/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class MeasurementPoint implements MacroConnectionSuffix {

    private static final String MEASURE_PREFIX = "Measure";

    private final String idSuffix;
    private final String connectionSuffix;

    private MeasurementPoint(String idSuffix, String connectionSuffix) {
        this.idSuffix = idSuffix;
        this.connectionSuffix = connectionSuffix;
    }

    public static MeasurementPoint of(int i) {
        if (i < 1) {
            throw new IllegalArgumentException();
        }
        return new MeasurementPoint(MEASURE_PREFIX + i, i != 1 ? String.valueOf(i) : "");
    }

    public String getIdSuffix() {
        return idSuffix;
    }

    public String getConnectionSuffix() {
        return connectionSuffix;
    }
}
