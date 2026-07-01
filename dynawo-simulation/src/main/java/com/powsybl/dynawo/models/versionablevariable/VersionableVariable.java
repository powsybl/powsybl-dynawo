/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.versionablevariable;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.commons.DynawoVersion;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class VersionableVariable {

    public record VariableStep(DynawoVersion versionMin, String variable) {
        public VariableStep(String variable) {
            this(DynawoConstants.VERSION_MIN, variable);
        }
    }

    private final List<VariableStep> steps;

    private String currentValue;

    public VersionableVariable(VariableStep... steps) {
        this.steps = List.of(steps);
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(DynawoVersion currentVersion) {
        for (int i = steps.size() - 1; i >= 0; i--) {
            VariableStep step = steps.get(i);
            if (step.versionMin().compareTo(currentVersion) <= 0) {
                currentValue = step.variable();
                return;
            }
        }
        throw new PowsyblException("No VersionableVariable value found for Dynawo version %s".formatted(currentVersion));
    }
}
