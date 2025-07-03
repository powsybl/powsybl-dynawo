/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.powsybl.dynawo.json.DynawoSimulationParametersSerializer;
import com.powsybl.dynawo.margincalculation.MarginCalculationParameters;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class MarginCalculationParametersDeserializer extends StdDeserializer<MarginCalculationParameters> {

    MarginCalculationParametersDeserializer() {
        super(MarginCalculationParameters.class);
    }

    @Override
    public MarginCalculationParameters deserialize(JsonParser parser, DeserializationContext context) throws IOException {

        MarginCalculationParameters.Builder builder = MarginCalculationParameters.builder();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.currentName()) {
                case "version" -> parser.nextToken();
                case "startTime" -> builder.setStartTime(parser.getValueAsDouble());
                case "stopTime" -> builder.setStopTime(parser.getValueAsDouble());
                case "debugDir" -> builder.setDebugDir(parser.getValueAsString());
                case "marginCalculationStartTime" -> builder.setMarginCalculationStartTime(parser.getValueAsDouble());
                case "loadIncreaseStartTime" -> builder.setLoadIncreaseStartTime(parser.getValueAsDouble());
                case "loadIncreaseStopTime" -> builder.setLoadIncreaseStopTime(parser.getValueAsDouble());
                case "contingenciesStartTime" -> builder.setContingenciesStartTime(parser.getValueAsDouble());
                case "calculationType" -> {
                    parser.nextToken();
                    builder.setCalculationType(parser.readValueAs(MarginCalculationParameters.CalculationType.class));
                }
                case "accuracy" -> builder.setAccuracy(parser.getValueAsInt());
                case "loadModelsRule" -> {
                    parser.nextToken();
                    builder.setLoadModelsRule(parser.readValueAs(MarginCalculationParameters.LoadModelsRule.class));
                }
                case "dynawoParameters" -> {
                    parser.nextToken();
                    builder.setDynawoParameters(new DynawoSimulationParametersSerializer().deserialize(parser, context));
                }
                default -> throw new IllegalStateException("Unexpected field: " + parser.currentName());
            }
        }
        return builder.build();
    }
}
