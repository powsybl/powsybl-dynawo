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
import com.google.auto.service.AutoService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
        Assertions.assertThat(configs.keySet()).containsExactly("synchronousGenerators");
        ModelConfigs synchroGens = configs.get("synchronousGenerators");
        Assertions.assertThat(synchroGens.getSupportedLibs()).containsExactly(
                "WeccCs",
                "WT4BWeccCurrentSource",
                "WT4AWeccCurrentSource");
        ModelConfig defaultModel = new ModelConfig("WT4BWeccCurrentSource", null, null, List.of("SYNCHRONIZED", "CONTROLLABLE"), true);
        Assertions.assertThat(synchroGens.getModelConfigMap().values()).containsExactly(
                new ModelConfig("PhotovoltaicsWeccCurrentSource", "WeccCs", "WTG4A", List.of("SYNCHRONIZED"), false),
                defaultModel,
                new ModelConfig("WT4AWeccCurrentSource", null, null, Collections.emptyList(), false));
        assertEquals(defaultModel, synchroGens.getDefaultModelConfig());
    }

    @Test
    void loadServices() {
        ModelConfigsHandler handler = ModelConfigsHandler.getInstance();
        Assertions.assertThat(handler.getModelConfigsNew("category1").getModelConfigMap().values()).containsExactly(
                new ModelConfig("AA", null, null, Collections.emptyList(), true));
        Assertions.assertThat(handler.getModelConfigsNew("phaseShiftersI").getModelConfigMap().values()).containsExactlyInAnyOrder(
                new ModelConfig("PhaseShifterI", null, null, Collections.emptyList(), false),
                new ModelConfig("PhaseShifterI2", null, null, Collections.emptyList(), true),
                new ModelConfig("PhaseShifterI3", null, null, Collections.emptyList(), false));
    }

    @AutoService(ModelConfigLoader.class)
    public static class ModelConfigLoader1 implements ModelConfigLoader {

        @Override
        public String getModelConfigFileName() {
            return null;
        }

        @Override
        public Map<String, ModelConfigs> loadModelConfigs() {
            ModelConfig defaultModel = new ModelConfig("AA", null, null, Collections.emptyList(), true);
            ModelConfigs cat1ModelConfigs = new ModelConfigs(new HashMap<>(Map.of(defaultModel.name(), defaultModel)), defaultModel);
            ModelConfig ps1 = new ModelConfig("PhaseShifterI2", null, null, Collections.emptyList(), true);
            ModelConfig ps2 = new ModelConfig("PhaseShifterI3", null, null, Collections.emptyList(), false);
            ModelConfigs psModelConfigs = new ModelConfigs(new HashMap<>(Map.of(ps1.name(), ps1, ps2.name(), ps2)), ps1);
            return Map.of("category1", cat1ModelConfigs,
                    "phaseShiftersI", psModelConfigs);
        }

        @Override
        public Stream<BuilderConfig> loadBuilderConfigs() {
            return null;
        }
    }
}
