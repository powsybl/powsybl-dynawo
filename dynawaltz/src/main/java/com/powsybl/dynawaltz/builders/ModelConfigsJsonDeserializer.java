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
import java.util.concurrent.atomic.AtomicReference;

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
        AtomicReference<String> defaultLib = new AtomicReference<>();
        final Map<String, ModelConfig> libs = new HashMap<>();
        JsonUtil.parseObject(parser , name ->
            switch (name) {
                case "defaultLib" -> {
                    defaultLib.set(parser.nextTextValue());
                    yield true;
                }
                case "libs" -> {
                    JsonUtil.parseObjectArray(parser, mc -> libs.put(mc.name(), mc), ModelConfigsJsonDeserializer::parseModelConfig);
                    yield true;
                }
                default -> false;
        });
        return new ModelConfigs(libs, defaultLib.get());
    }

    private static ModelConfig parseModelConfig(JsonParser parser) {
        AtomicReference<String> lib = new AtomicReference<>();
        AtomicReference<String> alias = new AtomicReference<>();
        AtomicReference<String> internalModelPrefix = new AtomicReference<>();
        List<String> properties = new ArrayList<>(0);
        JsonUtil.parseObject(parser , name ->
            switch (parser.getCurrentName()) {
                case "lib" -> {
                    lib.set(parser.nextTextValue());
                    yield true;
                }
                case "properties" -> {
                    properties.addAll(JsonUtil.parseStringArray(parser));
                    yield true;
                }
                case "internalModelPrefix" -> {
                    internalModelPrefix.set(parser.nextTextValue());
                    yield true;
                }
                case "alias" -> {
                    alias.set(parser.nextTextValue());
                    yield true;
                }
                default -> false;
        });
        return new ModelConfig(lib.get(), alias.get(), internalModelPrefix.get(), properties);
    }
}
