/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.versionableVariable;

import com.powsybl.dynawo.commons.DynawoVersion;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class VariableResolver {

    private final DynawoVersion currentVersion;

    public VariableResolver(DynawoVersion currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String resolve(VersionableVariable versionableVariable) {
        //TODO handle multiple steps
        List<VersionableVariable.VariableStep> steps = versionableVariable.getSteps();
        if (steps.getLast().versionMin().compareTo(currentVersion) <= 0) {
            return steps.getLast().variable();
        }
        return steps.getFirst().variable();
    }
}
