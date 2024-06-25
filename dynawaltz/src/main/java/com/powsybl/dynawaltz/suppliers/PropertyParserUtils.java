/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.json.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class PropertyParserUtils {

    private PropertyParserUtils() {
    }

    public static Property parseProperty(JsonParser parser) {
        PropertyBuilder builder = new PropertyBuilder();
        JsonUtil.parseObject(parser, name -> {
            boolean handled = true;
            switch (name) {
                case "name" -> builder.name(parser.nextTextValue());
                case "value" -> builder.value(parser.nextTextValue());
                case "values" -> builder.values(JsonUtil.parseStringArray(parser));
                case "arrays" -> parseArrays(parser, builder);
                case "type" -> builder.type(PropertyType.valueOf(parser.nextTextValue()));
                default -> handled = false;
            }
            return handled;
        });
        return builder.build();
    }

    private static void parseArrays(JsonParser parser, PropertyBuilder builder) throws IOException {
        parser.nextToken();
        parser.nextToken();
        JsonToken token;
        List<List<String>> arrays = new ArrayList<>();
        List<String> values = new ArrayList<>();
        while ((token = parser.nextToken()) != null) {
            if (token == JsonToken.VALUE_STRING) {
                values.add(parser.getText());
            } else if (token == JsonToken.END_ARRAY) {
                arrays.add(values);
                JsonToken next = parser.nextToken();
                if (next == JsonToken.END_ARRAY) {
                    builder.arrays(arrays);
                    break;
                } else if (next == JsonToken.START_ARRAY) {
                    values = new ArrayList<>();
                } else {
                    throw new PowsyblException("Unexpected token " + next);
                }
            } else {
                throw new PowsyblException("Unexpected token " + token);
            }
        }
    }
}
