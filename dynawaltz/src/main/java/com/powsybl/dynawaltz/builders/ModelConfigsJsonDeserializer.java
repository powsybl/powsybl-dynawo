/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.powsybl.commons.json.JsonUtil;

import java.io.IOException;
import java.util.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ModelConfigsJsonDeserializer extends StdDeserializer<Map<String, ModelConfigs>> {

    public ModelConfigsJsonDeserializer() {
        super(Map.class);
    }

    @Override
    public Map<String, ModelConfigs> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        Map<String, ModelConfigs> configMap = new HashMap<>();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String category = parser.getCurrentName();
            parser.nextToken();
            configMap.put(category, parseModelConfigs(parser));
        }
        return configMap;
    }

    private static ModelConfigs parseModelConfigs(JsonParser parser) {
        var parsingContext = new Object() {
            String defaultLib = null;
            final Map<String, ModelConfig> libs = new HashMap<>();
        };
        JsonUtil.parseObject(parser, name ->
            switch (name) {
                case "defaultLib" -> {
                    parsingContext.defaultLib = parser.nextTextValue();
                    yield true;
                }
                case "libs" -> {
                    JsonUtil.parseObjectArray(parser, mc -> parsingContext.libs.put(mc.name(), mc), ModelConfigsJsonDeserializer::parseModelConfig);
                    yield true;
                }
                default -> false;
            }
        );
        return new ModelConfigs(parsingContext.libs, parsingContext.defaultLib);
    }

    private static ModelConfig parseModelConfig(JsonParser parser) {
        var parsingContext = new Object() {
            String lib = null;
            String alias = null;
            String internalModelPrefix = null;
            String doc = null;
            List<String> properties = Collections.emptyList();
        };
        JsonUtil.parseObject(parser, name ->
            switch (parser.getCurrentName()) {
                case "lib" -> {
                    parsingContext.lib = parser.nextTextValue();
                    yield true;
                }
                case "properties" -> {
                    parsingContext.properties = JsonUtil.parseStringArray(parser);
                    yield true;
                }
                case "internalModelPrefix" -> {
                    parsingContext.internalModelPrefix = parser.nextTextValue();
                    yield true;
                }
                case "alias" -> {
                    parsingContext.alias = parser.nextTextValue();
                    yield true;
                }
                case "doc" -> {
                    parsingContext.doc = parser.nextTextValue();
                    yield true;
                }
                default -> false;
            }
        );
        return new ModelConfig(parsingContext.lib, parsingContext.alias, parsingContext.internalModelPrefix, parsingContext.properties, parsingContext.doc);
    }
}
