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
import java.util.List;
import java.util.Map;

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
                      "properties": [
                        "Synchronized",
                        "Controllable"
                      ]
                    },
                    {
                      "lib": "WT4AWeccCurrentSource"
                    }
                ]
                }""";

        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Map.class, new ModelConfigsJsonDeserializer());
        objectMapper.registerModule(module);
        Map<String, Map<String, ModelConfig>> configs = objectMapper.readValue(json, new TypeReference<>() {
        });
        Assertions.assertThat(configs.keySet()).containsExactly("synchronousGenerators");
        Assertions.assertThat(configs.get("synchronousGenerators").keySet()).containsExactly(
                "WeccCs",
                "WT4BWeccCurrentSource",
                "WT4AWeccCurrentSource");
        Assertions.assertThat(configs.get("synchronousGenerators").values()).containsExactly(
                new ModelConfig("PhotovoltaicsWeccCurrentSource", "WeccCs", "WTG4A", List.of("SYNCHRONIZED")),
                new ModelConfig("WT4BWeccCurrentSource", null, null, List.of("SYNCHRONIZED", "CONTROLLABLE")),
                new ModelConfig("WT4AWeccCurrentSource", null, null, Collections.emptyList()));
    }
}
