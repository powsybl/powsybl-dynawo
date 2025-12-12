/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResult;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResults;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationResultsSerializer extends StdSerializer<CriticalTimeCalculationResults> {

    private static final String VERSION = "1.0";

    CriticalTimeCalculationResultsSerializer() {
        super(CriticalTimeCalculationResults.class);
    }

    public static void write(CriticalTimeCalculationResults results, Path jsonFile) {
        Objects.requireNonNull(results);
        Objects.requireNonNull(jsonFile);
        try {
            try (OutputStream os = Files.newOutputStream(jsonFile)) {
                ObjectMapper objectMapper = JsonUtil.createObjectMapper();
                SimpleModule module = new SimpleModule();
                module.addSerializer(CriticalTimeCalculationResults.class, new CriticalTimeCalculationResultsSerializer());
                objectMapper.registerModule(module);
                ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
                writer.writeValue(os, results);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void serialize(CriticalTimeCalculationResults results, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("version", VERSION);
        jsonGenerator.writeFieldName("scenarioResults");
        jsonGenerator.writeStartArray();
        for (CriticalTimeCalculationResult scenarioResult : results.getCriticalTimeCalculationResults()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", scenarioResult.id());
            jsonGenerator.writeStringField("status", scenarioResult.status().toString());
            if (!Double.isNaN(scenarioResult.criticalTime())) {
                jsonGenerator.writeNumberField("criticalTime", scenarioResult.criticalTime());
            }
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
