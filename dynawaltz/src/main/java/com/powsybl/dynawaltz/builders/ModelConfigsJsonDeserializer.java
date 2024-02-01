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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.fasterxml.jackson.core.JsonToken.VALUE_STRING;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ModelConfigsJsonDeserializer extends StdDeserializer<Map<String, ModelConfigs>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelConfigsJsonDeserializer.class);

    public ModelConfigsJsonDeserializer() {
        super(Map.class);
    }

    @Override
    public Map<String, ModelConfigs> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        Map<String, ModelConfigs> configMap = new HashMap<>();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String category = parser.getCurrentName();
            parser.nextToken();
            configMap.put(category, deserializeModelConfigs(parser));
        }
        return configMap;
    }

    private ModelConfigs deserializeModelConfigs(JsonParser parser) throws IOException {
        Map<String, ModelConfig> configs = new HashMap<>();
        boolean defaultAlreadySet = false;
        ModelConfig defaultModelConfig = null;
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            String lib = null;
            String alias = null;
            String internalModelPrefix = null;
            List<String> properties = Collections.emptyList();
            boolean isDefaultLib = false;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                switch (parser.getCurrentName()) {
                    case "lib":
                        lib = parser.getValueAsString();
                        break;
                    case "properties":
                        properties = deserializeProperties(parser);
                        break;
                    case "internalModelPrefix":
                        internalModelPrefix = parser.getValueAsString();
                        break;
                    case "alias":
                        alias = parser.getValueAsString();
                        break;
                    case "default":
                        isDefaultLib = parser.getValueAsBoolean();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected field: " + parser.getCurrentName());
                }
            }
            if (isDefaultLib) {
                if (defaultAlreadySet) {
                    LOGGER.warn("{} can't be set as default, the default lib is already set", lib);
                    isDefaultLib = false;
                } else {
                    defaultAlreadySet = true;
                }
            }
            ModelConfig modelConfig = new ModelConfig(lib, alias, internalModelPrefix, properties, isDefaultLib);
            configs.put(modelConfig.name(), modelConfig);
            if (isDefaultLib) {
                defaultModelConfig = modelConfig;
            }
        }
        return new ModelConfigs(configs, defaultModelConfig);
    }

    private List<String> deserializeProperties(JsonParser parser) throws IOException {
        List<String> properties = new ArrayList<>();
        JsonToken token;
        while ((token = parser.nextToken()) != JsonToken.END_ARRAY) {
            if (token == VALUE_STRING) {
                properties.add(parser.getValueAsString().toUpperCase());
            }
        }
        return properties;
    }
}
