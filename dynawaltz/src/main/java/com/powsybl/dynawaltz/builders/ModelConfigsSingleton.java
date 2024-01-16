/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ModelConfigsSingleton {

    private static final ModelConfigsSingleton INSTANCE = new ModelConfigsSingleton();

    private final List<ModelConfigLoader> modelConfigLoaders;
    private final Map<String, Map<String, ModelConfig>> modelConfigs = new HashMap<>();

    private ModelConfigsSingleton() {
        modelConfigLoaders = Lists.newArrayList(ServiceLoader.load(ModelConfigLoader.class));
        modelConfigLoaders.forEach(l -> l.loadModelConfigs().forEach(
                (cat, modelsMap) -> modelConfigs.merge(cat, modelsMap, (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                })
        ));
    }

    public static ModelConfigsSingleton getInstance() {
        return INSTANCE;
    }

    public Map<String, ModelConfig> getModelConfigs(String categoryName) {
        return modelConfigs.get(categoryName);
    }

    public List<BuilderConfig> getBuilderConfigs() {
        return modelConfigLoaders.stream().flatMap(ModelConfigLoader::loadBuilderConfigs).toList();
    }
}
