/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.dynawo.commons.DynawoVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ModelConfigs {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelConfigs.class);

    private ModelConfig defaultModelConfig;
    private final SortedMap<String, ModelConfig> modelConfigMap;

    ModelConfigs(SortedMap<String, ModelConfig> modelConfigMap, String defaultModelConfigName) {
        this.modelConfigMap = Objects.requireNonNull(modelConfigMap);
        if (defaultModelConfigName != null) {
            this.defaultModelConfig = Objects.requireNonNull(modelConfigMap.get(defaultModelConfigName));
        }
    }

    public boolean hasDefaultModelConfig() {
        return defaultModelConfig != null;
    }

    public ModelConfig getDefaultModelConfig() {
        return defaultModelConfig;
    }

    public ModelConfig getModelConfig(String modelName) {
        return modelConfigMap.get(modelName);
    }

    public Collection<ModelInfo> getModelInfos() {
        return Collections.unmodifiableCollection(modelConfigMap.values());
    }

    public Collection<ModelInfo> getModelInfos(DynawoVersion dynawoVersion) {
        return modelConfigMap.values().stream()
                .filter(m -> m.version().includes(dynawoVersion))
                .collect(Collectors.toUnmodifiableList());
    }

    Set<String> getModelsName() {
        return Collections.unmodifiableSet(modelConfigMap.keySet());
    }

    void addModelConfigs(ModelConfigs modelConfigsToMerge) {
        if (hasDefaultModelConfig() && modelConfigsToMerge.hasDefaultModelConfig()) {
            LOGGER.warn("Default model configs {} & {} found, the first one will be kept",
                    defaultModelConfig.lib(),
                    modelConfigsToMerge.getDefaultModelConfig().lib());
        } else if (!hasDefaultModelConfig()) {
            defaultModelConfig = modelConfigsToMerge.defaultModelConfig;
        }
        modelConfigMap.putAll(modelConfigsToMerge.modelConfigMap);
    }
}
