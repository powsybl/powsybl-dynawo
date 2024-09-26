/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.dynawo.commons.DynawoVersion;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record VersionBound(DynawoVersion min, DynawoVersion max, String terminationCause) {

    public static final DynawoVersion MODEL_DEFAULT_MIN_VERSION = new DynawoVersion(1, 5, 0);

    public static VersionBound createDefaultVersion() {
        return new VersionBound(MODEL_DEFAULT_MIN_VERSION);
    }

    public VersionBound(DynawoVersion min) {
        this(min, null, null);
    }

    public boolean isBetween(DynawoVersion version) {
        return hasMinBound(version) && hasMaxBound(version);
    }

    public boolean hasMinBound(DynawoVersion version) {
        return version.compareTo(min) >= 0;
    }

    public boolean hasMaxBound(DynawoVersion version) {
        return max == null || version.compareTo(max) <= 0;
    }

    public String formattedInfo() {
        if (max == null) {
            return "Dynawo Version " + min;
        }
        return String.format("Dynawo Version %s - %s (%s)", min, max, terminationCause);
    }
}
