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
import com.powsybl.dynawo.models.VarMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolve all VarConnection variable with a value dependent of the Dynawo version
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class VersionableVariablesHandler {

    public static final DynawoVersion DYNAWO_VERSION_1_8_0 = new DynawoVersion(1, 8, 0);
    private static final VersionableVariablesHandler INSTANCE = new VersionableVariablesHandler();

    private final Map<String, String> currentVariableValues = new HashMap<>();
    private final Map<String, List<VarMapping>> currentVarMappingsValues = new HashMap<>();

    private DynawoVersion currentVersion = DynawoConstants.CURRENT_VERSION;

    private VersionableVariablesHandler() {
    }

    public static VersionableVariablesHandler getInstance() {
        return INSTANCE;
    }

    public String getCurrentVariable(VersionableVariable versionableVariable) {
        return currentVariableValues.computeIfAbsent(versionableVariable.getName(),
                k -> getCurrentValue(versionableVariable));
    }

    public String getCurrentVariable(VersionableVariable versionableVariable, Object... args) {
        return String.format(currentVariableValues.computeIfAbsent(versionableVariable.getName(),
                k -> getCurrentValue(versionableVariable)), args);
    }

    public String getCurrentValue(String name, Object... args) {
        return String.format(currentVariableValues.get(name), args);
    }

    public void setCurrentVersion(DynawoVersion currentVersion) {
        if (this.currentVersion.compareTo(currentVersion) != 0) {
            this.currentVersion = currentVersion;
            currentVariableValues.clear();
            currentVarMappingsValues.clear();
        }
    }

    /**
     * Load lazily the current value for VersionableVarsMapping
     */
    public List<VarMapping> getCurrentVarMapping(VersionableVarsMapping versionableVarsMapping) {
        return currentVarMappingsValues.computeIfAbsent(versionableVarsMapping.getName(),
                k -> getCurrentValue(versionableVarsMapping));
    }

    /**
     * Resolve Versionable current value without storing it
     */
    public <T> T getCurrentVersionable(Versionable<T> versionable) {
        return getCurrentValue(versionable);
    }

    private <T> T getCurrentValue(Versionable<T> versionable) {
        for (int i = versionable.getSteps().size() - 1; i >= 0; i--) {
            Versionable.Step<T> step = versionable.getSteps().get(i);
            if (step.versionMin().compareTo(currentVersion) <= 0) {
                return step.value();
            }
        }
        throw new PowsyblException("No versionable value found for Dynawo version %s".formatted(currentVersion));
    }
}
