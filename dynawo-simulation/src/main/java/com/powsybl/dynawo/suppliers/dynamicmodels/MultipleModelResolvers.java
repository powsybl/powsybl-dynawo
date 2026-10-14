/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.dynamicmodels;

import com.google.common.base.Suppliers;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Registry for {@link MultipleModelResolver} instances, loaded via ServiceLoader.
 *
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class MultipleModelResolvers {

    private static final Supplier<Map<String, MultipleModelResolver>> MULTIPLE_MODEL_RESOLVER_SUPPLIER =
            Suppliers.memoize(() -> ServiceLoader.load(MultipleModelResolver.class).stream()
                    .map(ServiceLoader.Provider::get)
                    .collect(Collectors.toMap(MultipleModelResolver::getName, resolver -> resolver)));

    private final Map<String, MultipleModelResolver> resolverMap;

    public MultipleModelResolvers() {
        this.resolverMap = MULTIPLE_MODEL_RESOLVER_SUPPLIER.get();
    }

    public MultipleModelResolver getMultipleModelResolver(String name) {
        return resolverMap.get(name);
    }
}
