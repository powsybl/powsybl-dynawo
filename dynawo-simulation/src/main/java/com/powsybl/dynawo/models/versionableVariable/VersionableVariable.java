/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.versionableVariable;

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

    public VersionableVariable(String baseVariable, VariableStep step) {
        this.steps = List.of(new VariableStep(baseVariable), step);
    }

    public List<VariableStep> getSteps() {
        return steps;
    }
}
