/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ModelConfigs {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelConfigs.class);

    private final ModelConfig defaultModelConfig;
    private final Map<String, ModelConfig> modelConfigMap;

    ModelConfigs(Map<String, ModelConfig> modelConfigMap, ModelConfig defaultModelConfig) {
        this.modelConfigMap = modelConfigMap;
        this.defaultModelConfig = defaultModelConfig;
    }

    public boolean hasDefaultModelConfig() {
        return defaultModelConfig != null;
    }

    public ModelConfig getDefaultModelConfig() {
        return defaultModelConfig;
    }

    public ModelConfig getModelConfig(String libName) {
        return modelConfigMap.get(libName);
    }

    public Set<String> getSupportedLibs() {
        return modelConfigMap.keySet();
    }

    void addModelConfigs(ModelConfigs modelConfigsToMerge) {
        modelConfigMap.putAll(modelConfigsToMerge.modelConfigMap);
        if (hasDefaultModelConfig() && modelConfigsToMerge.hasDefaultModelConfig()) {
            ModelConfig extraDefaultModelConfig = modelConfigsToMerge.getDefaultModelConfig();
            LOGGER.warn("Default model configs {} & {} found, the first one will be kept",
                    getDefaultModelConfig().name(),
                    extraDefaultModelConfig.name());
            modelConfigsToMerge.modelConfigMap.replace(extraDefaultModelConfig.name(), ModelConfig.copyOf(extraDefaultModelConfig, false));
        }
        modelConfigMap.putAll(modelConfigsToMerge.modelConfigMap);
    }
}
