/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.versionablevariable;

import com.google.common.base.Suppliers;
import com.powsybl.dynawo.commons.DynawoVersion;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class VersionableVariablesHandler {

    private static final Supplier<List<VersionableVariablesResolver>> RESOLVER_SUPPLIER =
            Suppliers.memoize(() -> ServiceLoader.load(VersionableVariablesResolver.class).stream()
                    .map(ServiceLoader.Provider::get)
                    .collect(Collectors.toList()));

    private final List<VersionableVariablesResolver> resolvers;

    public VersionableVariablesHandler() {
        this.resolvers = RESOLVER_SUPPLIER.get();
    }

    public void setCurrentValues(DynawoVersion currentVersion) {
        resolvers.forEach(p -> p.setCurrentValues(currentVersion));
    }
}
