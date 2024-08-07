/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.events;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynawo.suppliers.Property;
import com.powsybl.dynawo.suppliers.PropertyParserUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class EventModelConfigsJsonDeserializer extends StdDeserializer<List<EventModelConfig>> {

    public EventModelConfigsJsonDeserializer() {
        super(List.class);
    }

    @Override
    public List<EventModelConfig> deserialize(JsonParser parser, DeserializationContext context) {
        List<EventModelConfig> modelConfigList = new ArrayList<>();
        JsonUtil.parseObject(parser, name -> {
            if (name.equals("events")) {
                JsonUtil.parseObjectArray(parser, modelConfigList::add, EventModelConfigsJsonDeserializer::parseModelConfig);
                return true;
            }
            return false;
        });
        return modelConfigList;
    }

    private static EventModelConfig parseModelConfig(JsonParser parser) {
        var parsingContext = new Object() {
            String model = null;
            final List<Property> properties = new ArrayList<>();
        };
        JsonUtil.parseObject(parser, name -> {
            boolean handled = true;
            switch (name) {
                case "model" -> parsingContext.model = parser.nextTextValue();
                case "properties" -> JsonUtil.parseObjectArray(parser, parsingContext.properties::add, PropertyParserUtils::parseProperty);
                default -> handled = false;
            }
            return handled;
        });
        return new EventModelConfig(parsingContext.model, parsingContext.properties);
    }
}
