/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.powsybl.dynawo.json.DynawoSimulationParametersSerializer;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationParameters;

import java.io.IOException;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationParametersDeserializer extends StdDeserializer<CriticalTimeCalculationParameters> {

    CriticalTimeCalculationParametersDeserializer() {
        super(CriticalTimeCalculationParameters.class);
    }

    @Override
    public CriticalTimeCalculationParameters deserialize(JsonParser parser, DeserializationContext context) throws IOException {

        CriticalTimeCalculationParameters.Builder builder = CriticalTimeCalculationParameters.builder();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.currentName()) {
                case "version" -> parser.nextToken();
                case "startTime" -> builder.setStartTime(parser.getValueAsDouble());
                case "stopTime" -> builder.setStopTime(parser.getValueAsDouble());
                case "faultTimeMin" -> builder.setMinValue(parser.getValueAsDouble());
                case "faultTimeMax" -> builder.setMaxValue(parser.getValueAsDouble());
                case "debugDir" -> builder.setDebugDir(parser.getValueAsString());
                case "mode" -> {
                    parser.nextToken();
                    builder.setMode(parser.readValueAs(CriticalTimeCalculationParameters.Mode.class));
                }
                case "accuracy" -> builder.setAccuracy(parser.getValueAsDouble());
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
