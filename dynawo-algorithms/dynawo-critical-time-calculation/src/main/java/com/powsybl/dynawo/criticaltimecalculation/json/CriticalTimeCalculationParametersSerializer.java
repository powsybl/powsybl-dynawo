/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationParameters;
import com.powsybl.dynawo.json.DynawoSimulationParametersSerializer;

import java.io.IOException;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationParametersSerializer extends StdSerializer<CriticalTimeCalculationParameters> {
    CriticalTimeCalculationParametersSerializer() {
        super(CriticalTimeCalculationParameters.class);
    }

    @Override
    public void serialize(CriticalTimeCalculationParameters parameters, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("startTime", parameters.getStartTime());
        jsonGenerator.writeNumberField("stopTime", parameters.getStopTime());
        if (parameters.getDebugDir() != null) {
            jsonGenerator.writeStringField("debugDir", parameters.getDebugDir());
        }
        jsonGenerator.writeNumberField("accuracy", parameters.getAccuracy());
        jsonGenerator.writeNumberField("faultTimeMin", parameters.getMinValue());
        jsonGenerator.writeNumberField("faultTimeMax", parameters.getMaxValue());
        jsonGenerator.writeStringField("mode", parameters.getMode().toString());
        jsonGenerator.writeFieldName("dynawoParameters");
        new DynawoSimulationParametersSerializer().serialize(parameters.getDynawoParameters(), jsonGenerator, serializerProvider);
        jsonGenerator.writeEndObject();
    }
}
