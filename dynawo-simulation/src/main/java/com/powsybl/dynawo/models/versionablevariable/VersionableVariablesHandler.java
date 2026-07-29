/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.versionablevariable;

import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.commons.DynawoVersion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * Resolve all VarConnection variable with a value dependent of the Dynawo version
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class VersionableVariablesHandler {

    private static final VersionableVariablesHandler INSTANCE = new VersionableVariablesHandler();

    private final List<VersionableVariable> versionableVariables;
    private final Map<String, String> currentValues = new HashMap<>();

    private DynawoVersion currentVersion = DynawoConstants.CURRENT_VERSION;

    private VersionableVariablesHandler() {
        this.versionableVariables = ServiceLoader.load(VersionableVariablesProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .flatMap(p -> p.getVersionableVariables().stream())
                .collect(Collectors.toList());
        setCurrentValues();
    }

    public static VersionableVariablesHandler getInstance() {
        return INSTANCE;
    }

    public String getCurrentValue(String name) {
        return currentValues.get(name);
    }

    public String getCurrentValue(String name, Object... args) {
        return String.format(currentValues.get(name), args);
    }

    public void setCurrentValues(DynawoVersion currentVersion) {
        if (this.currentVersion.compareTo(currentVersion) != 0) {
            this.currentVersion = currentVersion;
            setCurrentValues();
        }
    }

    private void setCurrentValues() {
        versionableVariables.forEach(vv -> currentValues.put(vv.name(), vv.getCurrentValue(currentVersion)));
    }
}
