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
 * Registry for {@link ModelResolver} and {@link MultipleModelResolver} instances, loaded via ServiceLoader.
 *
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ModelResolvers {

    private static final Supplier<Map<String, ModelResolver>> MODEL_RESOLVER_SUPPLIER =
            Suppliers.memoize(() -> ServiceLoader.load(ModelResolver.class).stream()
                    .map(ServiceLoader.Provider::get)
                    .collect(Collectors.toMap(ModelResolver::getName, modelResolver -> modelResolver)));

    private static final Supplier<Map<String, MultipleModelResolver>> MULTIPLE_MODEL_RESOLVER_SUPPLIER =
            Suppliers.memoize(() -> ServiceLoader.load(MultipleModelResolver.class).stream()
                    .map(ServiceLoader.Provider::get)
                    .collect(Collectors.toMap(MultipleModelResolver::getName, resolver -> resolver)));

    private final Map<String, ModelResolver> modelResolverMap;
    private final Map<String, MultipleModelResolver> multipleModelResolverMap;

    public ModelResolvers() {
        this.modelResolverMap = MODEL_RESOLVER_SUPPLIER.get();
        this.multipleModelResolverMap = MULTIPLE_MODEL_RESOLVER_SUPPLIER.get();
    }

    public ModelResolver getModelResolver(String name) {
        return modelResolverMap.get(name);
    }

    public MultipleModelResolver getMultipleModelResolver(String name) {
        return multipleModelResolverMap.get(name);
    }
}
