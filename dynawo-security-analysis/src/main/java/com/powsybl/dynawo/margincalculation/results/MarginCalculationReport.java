/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.results;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class MarginCalculationReport {

    private final List<LoadIncreaseResult> loadIncreaseResults;
    private byte[] logBytes;

    public static MarginCalculationReport empty() {
        return new MarginCalculationReport(Collections.emptyList());
    }

    public MarginCalculationReport(List<LoadIncreaseResult> loadIncreaseResults) {
        this.loadIncreaseResults = Objects.requireNonNull(loadIncreaseResults);
    }

    public List<LoadIncreaseResult> getResults() {
        return loadIncreaseResults;
    }

    /**
     * Gets log file in bytes.
     * @return an Optional describing the zip bytes
     */
    public Optional<byte[]> getLogBytes() {
        return Optional.ofNullable(logBytes);
    }

    public MarginCalculationReport setLogBytes(byte[] logBytes) {
        this.logBytes = logBytes;
        return this;
    }
}
