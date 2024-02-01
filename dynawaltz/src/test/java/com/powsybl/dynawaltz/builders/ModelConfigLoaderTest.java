/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ModelConfigLoaderTest {

    @Test
    void loadConfigTest() throws IOException {
        String json = """
                {
                "synchronousGenerators": [
                    {
                      "lib": "PhotovoltaicsWeccCurrentSource",
                      "alias": "WeccCs",
                      "internalModelPrefix": "WTG4A",
                      "properties": [
                        "Synchronized"
                      ]
                    },
                    {
                      "lib": "WT4BWeccCurrentSource",
                      "default": true,
                      "properties": [
                        "Synchronized",
                        "Controllable"
                      ]
                    },
                    {
                      "lib": "WT4AWeccCurrentSource",
                      "default": true
                    }
                ]
                }""";

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Map.class, new ModelConfigsJsonDeserializer());
        objectMapper.registerModule(module);
        Map<String, ModelConfigs> configs = objectMapper.readValue(json, new TypeReference<>() {
        });
        Assertions.assertThat(configs.keySet()).containsExactlyInAnyOrder("synchronousGenerators");
        ModelConfigs synchroGens = configs.get("synchronousGenerators");
        Assertions.assertThat(synchroGens.getSupportedLibs()).containsExactlyInAnyOrder(
                "WeccCs",
                "WT4BWeccCurrentSource",
                "WT4AWeccCurrentSource");
        ModelConfig defaultModel = new ModelConfig("WT4BWeccCurrentSource", null, null, List.of("SYNCHRONIZED", "CONTROLLABLE"), true);
        Assertions.assertThat(listModelConfigs(synchroGens)).containsExactlyInAnyOrder(
                new ModelConfig("PhotovoltaicsWeccCurrentSource", "WeccCs", "WTG4A", List.of("SYNCHRONIZED"), false),
                defaultModel,
                new ModelConfig("WT4AWeccCurrentSource", null, null, Collections.emptyList(), false));
        assertEquals(defaultModel, synchroGens.getDefaultModelConfig());
    }

    @Test
    void mergeModelConfigs() {
        ModelConfig defaultModel = new ModelConfig("AA", null, null, Collections.emptyList(), true);
        ModelConfigs modelConfigs1 = new ModelConfigs(new HashMap<>(Map.of(defaultModel.name(), defaultModel)), defaultModel);

        ModelConfig mc1 = new ModelConfig("BB", null, null, Collections.emptyList(), true);
        ModelConfig mc2 = new ModelConfig("CC", null, null, Collections.emptyList(), false);
        ModelConfigs modelConfigs2 = new ModelConfigs(new HashMap<>(Map.of(mc1.name(), mc1, mc2.name(), mc2)), mc1);

        modelConfigs1.addModelConfigs(modelConfigs2);
        Assertions.assertThat(listModelConfigs(modelConfigs1)).containsExactlyInAnyOrder(
                defaultModel,
                new ModelConfig("BB", null, null, Collections.emptyList(), false),
                mc2);

    }

    private List<ModelConfig> listModelConfigs(ModelConfigs modelConfigs) {
        return modelConfigs.getSupportedLibs().stream()
                .map(modelConfigs::getModelConfig)
                .toList();
    }
}
