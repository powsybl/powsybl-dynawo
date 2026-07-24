/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.dynamicmodels;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynawo.suppliers.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynamicModelConfigsJsonDeserializer extends StdDeserializer<DynamicModelConfigs> {

    public DynamicModelConfigsJsonDeserializer() {
        super(DynamicModelConfigs.class);
    }

    @Override
    public DynamicModelConfigs deserialize(JsonParser parser, DeserializationContext context) {
        List<DynamicModelConfig> modelConfigList = new ArrayList<>();
        List<DynamicAlternativeModelsConfig> altModelConfigList = new ArrayList<>();
        JsonUtil.parseObject(parser, name -> {
            if (name.equals("models")) {
                JsonUtil.parseObjectArray(parser, modelConfigList::add, DynamicModelConfigsJsonDeserializer::parseModelConfig);
                return true;
            } else if (name.equals("alternativeModels")) {
                JsonUtil.parseObjectArray(parser, altModelConfigList::add, DynamicModelConfigsJsonDeserializer::parseAlternativeModelConfig);
                return true;
            }
            return false;
        });
        return new DynamicModelConfigs(modelConfigList, altModelConfigList);
    }

    private static DynamicModelConfig parseModelConfig(JsonParser parser) {
        var parsingContext = new Object() {
            String model = null;
            String group = null;
            SetGroupType groupType = SetGroupType.FIXED;
            final List<Property> properties = new ArrayList<>();
        };
        JsonUtil.parseObject(parser, name -> {
            boolean handled = true;
            switch (name) {
                case "model" -> parsingContext.model = parser.nextTextValue();
                case "group" -> parsingContext.group = parser.nextTextValue();
                case "groupType" -> parsingContext.groupType = SetGroupType.valueOf(parser.nextTextValue());
                case "properties" -> JsonUtil.parseObjectArray(parser, parsingContext.properties::add, PropertyParserUtils::parseProperty);
                default -> handled = false;
            }
            return handled;
        });
        return new DynamicModelConfig(parsingContext.model, parsingContext.group, parsingContext.groupType, parsingContext.properties);
    }

    private static DynamicAlternativeModelsConfig parseAlternativeModelConfig(JsonParser parser) {
        var parsingContext = new Object() {
            final List<AlternativeModelConfig> alternativeModels = new ArrayList<>();
            String rule = null;
            SetGroupType groupType = SetGroupType.FIXED;
            final List<Property> properties = new ArrayList<>();
        };
        JsonUtil.parseObject(parser, name -> {
            boolean handled = true;
            switch (name) {
                case "alternativeModels" -> JsonUtil.parseObjectArray(parser, parsingContext.alternativeModels::add, DynamicModelConfigsJsonDeserializer::parseAlternativeModel);
                case "rule" -> parsingContext.rule = parser.nextTextValue();
                case "groupType" -> parsingContext.groupType = SetGroupType.valueOf(parser.nextTextValue());
                case "properties" -> JsonUtil.parseObjectArray(parser, parsingContext.properties::add, PropertyParserUtils::parseProperty);
                default -> handled = false;
            }
            return handled;
        });
        return new DynamicAlternativeModelsConfig(parsingContext.alternativeModels, parsingContext.rule,
                parsingContext.groupType, parsingContext.properties);
    }

    public static AlternativeModelConfig parseAlternativeModel(JsonParser parser) {
        var parsingContext = new Object() {
            String model = null;
            String group = null;
        };
        JsonUtil.parseObject(parser, name -> {
            boolean handled = true;
            switch (name) {
                case "model" -> parsingContext.model = parser.nextTextValue();
                case "group" -> parsingContext.group = parser.nextTextValue();
                default -> handled = false;
            }
            return handled;
        });
        return new AlternativeModelConfig(parsingContext.model, parsingContext.group);
    }
}
