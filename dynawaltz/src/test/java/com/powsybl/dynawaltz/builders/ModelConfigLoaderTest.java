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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ModelConfigLoaderTest {

    @Test
    void loadConfigTest() throws IOException {
        String json = """
                {
                    "synchronousGenerators": {
                        "defaultLib": "WT4BWeccCurrentSource",
                        "libs": [
                            {
                              "lib": "PhotovoltaicsWeccCurrentSource",
                              "alias": "Wecc",
                              "internalModelPrefix": "WTG4A",
                              "properties": [
                                "SYNCHRONIZED"
                              ],
                              "doc": "Photovoltaics Wecc generator"
                            },
                            {
                              "lib": "WT4BWeccCurrentSource",
                              "properties": [
                                "SYNCHRONIZED",
                                "CONTROLLABLE"
                              ]
                            },
                            {
                              "lib": "WT4AWeccCurrentSource",
                              "doc": "WT4A Wecc generator"
                            }
                        ]
                    }
                }""";

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Map.class, new ModelConfigsJsonDeserializer());
        objectMapper.registerModule(module);
        Map<String, ModelConfigs> configs = objectMapper.readValue(json, new TypeReference<>() {
        });
        assertThat(configs.keySet()).containsExactlyInAnyOrder("synchronousGenerators");
        ModelConfigs synchroGens = configs.get("synchronousGenerators");
        assertThat(synchroGens.getLibsName()).containsExactlyInAnyOrder(
                "Wecc",
                "WT4BWeccCurrentSource",
                "WT4AWeccCurrentSource");
        ModelConfig defaultModel = new ModelConfig("WT4BWeccCurrentSource", null, null, List.of("SYNCHRONIZED", "CONTROLLABLE"));
        assertThat(listModelConfigs(synchroGens)).containsExactlyInAnyOrder(
                new ModelConfig("PhotovoltaicsWeccCurrentSource", "Wecc", "WTG4A", List.of("SYNCHRONIZED"), "Photovoltaics Wecc generator"),
                defaultModel,
                new ModelConfig("WT4AWeccCurrentSource", null, null, Collections.emptyList(), "WT4A Wecc generator"));
        assertEquals(defaultModel, synchroGens.getDefaultModelConfig());
        assertThat(synchroGens.getLibsInfo()).containsExactlyInAnyOrder("Wecc (PhotovoltaicsWeccCurrentSource): Photovoltaics Wecc generator", "WT4BWeccCurrentSource", "WT4AWeccCurrentSource: WT4A Wecc generator");
    }

    @Test
    void mergeModelConfigs() {
        ModelConfig defaultModel = new ModelConfig("AA", null, null, Collections.emptyList());
        ModelConfigs modelConfigs1 = new ModelConfigs(new HashMap<>(Map.of(defaultModel.name(), defaultModel)), defaultModel.name());

        ModelConfig mc1 = new ModelConfig("BB", null, null, Collections.emptyList());
        ModelConfig mc2 = new ModelConfig("CC", null, null, Collections.emptyList());
        ModelConfigs modelConfigs2 = new ModelConfigs(new HashMap<>(Map.of(mc1.name(), mc1, mc2.name(), mc2)), mc1.name());

        modelConfigs1.addModelConfigs(modelConfigs2);
        assertThat(listModelConfigs(modelConfigs1)).containsExactlyInAnyOrder(
                defaultModel,
                new ModelConfig("BB", null, null, Collections.emptyList()),
                mc2);
        assertEquals(defaultModel, modelConfigs1.getDefaultModelConfig());

        ModelConfigs modelConfigs3 = new ModelConfigs(new HashMap<>(Map.of(mc2.name(), mc2)), null);
        ModelConfigs modelConfigs4 = new ModelConfigs(new HashMap<>(Map.of(defaultModel.name(), defaultModel)), defaultModel.name());
        modelConfigs3.addModelConfigs(modelConfigs4);
        assertEquals(defaultModel, modelConfigs3.getDefaultModelConfig());
    }

    private List<ModelConfig> listModelConfigs(ModelConfigs modelConfigs) {
        return modelConfigs.getLibsName().stream()
                .map(modelConfigs::getModelConfig)
                .toList();
    }
}
