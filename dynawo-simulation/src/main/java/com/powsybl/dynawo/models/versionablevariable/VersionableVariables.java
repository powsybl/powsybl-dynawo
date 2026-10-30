/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.versionablevariable;

import com.google.auto.service.AutoService;
import com.powsybl.dynawo.commons.DynawoVersion;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(VersionableVariablesProvider.class)
public final class VersionableVariables implements VersionableVariablesProvider {

    private static final DynawoVersion DYNAWO_VERSION_1_8_0 = new DynawoVersion(1, 8, 0);

    private static final List<VersionableVariable> VERSIONABLE_VARIABLES = List.of(
            new VersionableVariable("TC_LOCKED",
                    new VersionableVariable.VariableStep("tapChanger%s_locked"),
                    new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "transformer%s_locked")),
            new VersionableVariable("INDEXED_VARIABLE",
                    new VersionableVariable.VariableStep("%s_@INDEX@"),
                    new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "%s_@INDEX@_")),
            new VersionableVariable("INDEXED_VARIABLE_VALUE",
                    new VersionableVariable.VariableStep("%s_@INDEX@_value"),
                    new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "%s_@INDEX@_")),
            new VersionableVariable("VARIABLE_VALUE",
                    new VersionableVariable.VariableStep("%s_value"),
                    new VersionableVariable.VariableStep(DYNAWO_VERSION_1_8_0, "%s"))
    );

    @Override
    public List<VersionableVariable> getVersionableVariables() {
        return VERSIONABLE_VARIABLES;
    }
}
