/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.powsybl.dynawo.commons.DynawoVersion;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

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
                              "minVersion": "1.3.0",
                              "maxVersion": "1.4.0",
                              "endCause": "Deleted",
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
                              "doc": "WT4A Wecc generator",
                              "minVersion": "1.6.0"
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
        assertThat(configs.keySet()).containsExactly("synchronousGenerators");
        ModelConfigs synchroGens = configs.get("synchronousGenerators");
        assertThat(synchroGens.getModelsName()).containsExactly(
                "WT4AWeccCurrentSource",
                "WT4BWeccCurrentSource",
                "Wecc");
        ModelConfig defaultModel = new ModelConfig("WT4BWeccCurrentSource", List.of("SYNCHRONIZED", "CONTROLLABLE"));
        assertEquals(defaultModel, synchroGens.getDefaultModelConfig());
        assertThat(synchroGens.getModelInfos())
                .containsExactly(
                    new ModelConfig("WT4AWeccCurrentSource", null, null, Collections.emptyList(), "WT4A Wecc generator", new VersionInterval(new DynawoVersion(1, 6, 0))),
                    defaultModel,
                    new ModelConfig("PhotovoltaicsWeccCurrentSource", "Wecc", "WTG4A", List.of("SYNCHRONIZED"), "Photovoltaics Wecc generator", new VersionInterval(new DynawoVersion(1, 3, 0), new DynawoVersion(1, 4, 0), "Deleted")))
                // Check formatted info
                .map(ModelInfo::formattedInfo)
                .containsExactly(
                    "WT4AWeccCurrentSource: WT4A Wecc generator (Dynawo Version 1.6.0)",
                    "WT4BWeccCurrentSource (Dynawo Version 1.5.0)",
                    "Wecc (PhotovoltaicsWeccCurrentSource): Photovoltaics Wecc generator (Dynawo Version 1.3.0 - 1.4.0 (Deleted))");
        assertThat(synchroGens.getModelInfos(DynawoVersion.createFromString("1.5.0")))
                .map(ModelInfo::name)
                .hasSize(1)
                .containsExactly("WT4BWeccCurrentSource");
    }

    @Test
    void mergeModelConfigs() {
        ModelConfig defaultModel = new ModelConfig("AA");
        ModelConfigs modelConfigs1 = new ModelConfigs(new TreeMap<>(Map.of(defaultModel.name(), defaultModel)), defaultModel.name());

        ModelConfig mc1 = new ModelConfig("BB");
        ModelConfig mc2 = new ModelConfig("CC");
        ModelConfigs modelConfigs2 = new ModelConfigs(new TreeMap<>(Map.of(mc1.name(), mc1, mc2.name(), mc2)), mc1.name());

        modelConfigs1.addModelConfigs(modelConfigs2);
        assertThat(modelConfigs1.getModelInfos()).containsExactly(
                defaultModel,
                new ModelConfig("BB"),
                mc2);
        assertEquals(defaultModel, modelConfigs1.getDefaultModelConfig());

        ModelConfigs modelConfigs3 = new ModelConfigs(new TreeMap<>(Map.of(mc2.name(), mc2)), null);
        ModelConfigs modelConfigs4 = new ModelConfigs(new TreeMap<>(Map.of(defaultModel.name(), defaultModel)), defaultModel.name());
        modelConfigs3.addModelConfigs(modelConfigs4);
        assertEquals(defaultModel, modelConfigs3.getDefaultModelConfig());
    }
}
