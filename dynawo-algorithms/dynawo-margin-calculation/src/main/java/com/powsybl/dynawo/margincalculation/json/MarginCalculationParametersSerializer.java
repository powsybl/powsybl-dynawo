/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.powsybl.dynawo.json.DynawoSimulationParametersSerializer;
import com.powsybl.dynawo.margincalculation.MarginCalculationParameters;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class MarginCalculationParametersSerializer extends StdSerializer<MarginCalculationParameters> {

    MarginCalculationParametersSerializer() {
        super(MarginCalculationParameters.class);
    }

    @Override
    public void serialize(MarginCalculationParameters parameters, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("startTime", parameters.getStartTime());
        jsonGenerator.writeNumberField("stopTime", parameters.getStopTime());
        jsonGenerator.writeNumberField("marginCalculationStartTime", parameters.getMarginCalculationStartTime());
        jsonGenerator.writeNumberField("loadIncreaseStartTime", parameters.getLoadIncreaseStartTime());
        jsonGenerator.writeNumberField("loadIncreaseStopTime", parameters.getLoadIncreaseStopTime());
        jsonGenerator.writeNumberField("contingenciesStartTime", parameters.getContingenciesStartTime());
        jsonGenerator.writeStringField("calculationType", parameters.getCalculationType().toString());
        jsonGenerator.writeNumberField("accuracy", parameters.getAccuracy());
        jsonGenerator.writeStringField("loadModelsRule", parameters.getLoadModelsRule().toString());
        jsonGenerator.writeFieldName("dynawoParameters");
        new DynawoSimulationParametersSerializer().serialize(parameters.getDynawoParameters(), jsonGenerator, serializerProvider);
        jsonGenerator.writeEndObject();
    }
}
