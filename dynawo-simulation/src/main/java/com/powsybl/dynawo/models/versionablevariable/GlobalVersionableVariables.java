/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.versionablevariable;

import static com.powsybl.dynawo.models.versionablevariable.VersionableHandler.DYNAWO_VERSION_1_8_0;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class GlobalVersionableVariables {

    public static final VersionableVariable INDEXED_VARIABLE = new VersionableVariable("INDEXED_VARIABLE",
            new Versionable.Step<>("%s_@INDEX@"),
            new Versionable.Step<>(DYNAWO_VERSION_1_8_0, "%s_@INDEX@_"));

    public static final VersionableVariable INDEXED_VARIABLE_VALUE = new VersionableVariable("INDEXED_VARIABLE_VALUE",
            new Versionable.Step<>("%s_@INDEX@_value"),
            new Versionable.Step<>(DYNAWO_VERSION_1_8_0, "%s_@INDEX@_"));

    public static final VersionableVariable VARIABLE_VALUE = new VersionableVariable("VARIABLE_VALUE",
            new Versionable.Step<>("%s_value"),
            new Versionable.Step<>(DYNAWO_VERSION_1_8_0, "%s"));
}
