/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ModelConfigLoader {
//TODO a passer en service
    private static final String MODEL_CONFIG_FILENAME = "models.json";

    private ModelConfigLoader() {

    }

    public static Map<String, List<ModelConfig>> readModelConfigs() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(ModelConfigLoader.class.getClassLoader().getResource(MODEL_CONFIG_FILENAME), new TypeReference<>() {

            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ModelCategory> createCategories() {
        Map<String, List<ModelConfig>> modelsConfigs = readModelConfigs();
        return Arrays.stream(DynamicModelBuilderUtils.Categories.values()).map(c ->
            new ModelCategory(c.getCategoryName(), c.getConstructor(), modelsConfigs.get(c.getCategoryName())))
                .toList();
    }
}
