/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import java.util.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ModelConfigsSingleton {

    private static final ModelConfigsSingleton INSTANCE = new ModelConfigsSingleton();

    private final Map<String, List<ModelConfig>> modelConfigs = new HashMap<>();
    private final List<DynamicModelCategory> dynamicModelCategories = new ArrayList<>();

    private ModelConfigsSingleton() {
        for (ModelConfigLoader configLoader : ServiceLoader.load(ModelConfigLoader.class)) {
            configLoader.loadModelConfigs().forEach(
                    (cat, models) -> modelConfigs.merge(cat, models, (v1, v2) -> {
                        v1.addAll(v2);
                        return v1;
                    })
            );
            configLoader.loadBuilderCategories().forEach(bc ->
                dynamicModelCategories.add(new DynamicModelCategory(bc.getCategoryName(),
                        bc.getConstructor(), modelConfigs.get(bc.getCategoryName()))));
        }
    }

    public static ModelConfigsSingleton getInstance() {
        return INSTANCE;
    }

    //TODO use map of map instead ?
    //TODO handle category / lib not found
    public ModelConfig getModelConfig(String categoryName, String lib) {
        return modelConfigs.get(categoryName).stream()
                .filter(mc -> lib.equalsIgnoreCase(mc.getLib()))
                .findFirst()
                .orElse(null);
    }

    public ModelConfig getFirstModelConfig(String categoryName) {
        return modelConfigs.get(categoryName).get(0);
    }

    public List<DynamicModelCategory> getDynamicModelCategories() {
        return dynamicModelCategories;
    }
}
