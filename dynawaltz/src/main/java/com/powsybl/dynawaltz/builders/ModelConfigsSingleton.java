/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ModelConfigsSingleton {

    private static final ModelConfigsSingleton INSTANCE = new ModelConfigsSingleton();

    private final List<ModelConfigLoader> modelConfigLoaders;
    private final Map<String, List<ModelConfig>> modelConfigs = new HashMap<>();

    private ModelConfigsSingleton() {
        modelConfigLoaders = Lists.newArrayList(ServiceLoader.load(ModelConfigLoader.class));
        modelConfigLoaders.forEach(l -> l.loadModelConfigs().forEach(
                (cat, models) -> modelConfigs.merge(cat, models, (v1, v2) -> {
                    v1.addAll(v2);
                    return v1;
                })
        ));
    }

    public static ModelConfigsSingleton getInstance() {
        return INSTANCE;
    }

    //TODO create map at initialisation
    public Map<String, ModelConfig> getModelConfigs(String categoryName) {
        return modelConfigs.get(categoryName).stream()
                .collect(Collectors.toMap(ModelConfig::getName, Function.identity(), (o1, o2) -> o1, LinkedHashMap::new));
    }

    public List<BuilderConfig> getBuilderConfigs() {
        return modelConfigLoaders.stream().flatMap(ModelConfigLoader::loadBuilderConfigs).toList();
    }
}
