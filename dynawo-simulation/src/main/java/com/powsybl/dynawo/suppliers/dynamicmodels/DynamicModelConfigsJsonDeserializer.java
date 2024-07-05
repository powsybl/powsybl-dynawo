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
import com.powsybl.dynawo.suppliers.Property;
import com.powsybl.dynawo.suppliers.PropertyParserUtils;
import com.powsybl.dynawo.suppliers.SetGroupType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynamicModelConfigsJsonDeserializer extends StdDeserializer<List<DynamicModelConfig>> {

    public DynamicModelConfigsJsonDeserializer() {
        super(List.class);
    }

    @Override
    public List<DynamicModelConfig> deserialize(JsonParser parser, DeserializationContext context) {
        List<DynamicModelConfig> modelConfigList = new ArrayList<>();
        JsonUtil.parseObject(parser, name -> {
            if (name.equals("models")) {
                JsonUtil.parseObjectArray(parser, modelConfigList::add, DynamicModelConfigsJsonDeserializer::parseModelConfig);
                return true;
            }
            return false;
        });
        return modelConfigList;
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
}
