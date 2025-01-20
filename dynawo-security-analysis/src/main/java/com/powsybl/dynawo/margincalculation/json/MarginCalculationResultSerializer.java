/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynaflow.results.FailedCriterion;
import com.powsybl.dynaflow.results.ScenarioResult;
import com.powsybl.dynawo.margincalculation.results.LoadIncreaseResult;
import com.powsybl.dynawo.margincalculation.results.MarginCalculationResult;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class MarginCalculationResultSerializer extends StdSerializer<MarginCalculationResult> {

    private static final String VERSION = "1.0";

    MarginCalculationResultSerializer() {
        super(MarginCalculationResult.class);
    }

    public static void write(MarginCalculationResult result, Path jsonFile) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(jsonFile);
        try {
            try (OutputStream os = Files.newOutputStream(jsonFile)) {
                ObjectMapper objectMapper = JsonUtil.createObjectMapper();
                SimpleModule module = new SimpleModule();
                module.addSerializer(MarginCalculationResult.class, new MarginCalculationResultSerializer());
                objectMapper.registerModule(module);
                ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
                writer.writeValue(os, result);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void serialize(MarginCalculationResult result, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("version", VERSION);
        jsonGenerator.writeFieldName("loadIncreases");
        jsonGenerator.writeStartArray();
        for (LoadIncreaseResult loadIncrease : result.getResults()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("loadLevel", loadIncrease.loadLevel());
            jsonGenerator.writeStringField("status", loadIncrease.status().toString());
            writeFailedCriteria(loadIncrease.failedCriteria(), jsonGenerator);
            jsonGenerator.writeFieldName("scenarioResults");
            jsonGenerator.writeStartArray();
            for (ScenarioResult scenario : loadIncrease.scenarioResults()) {
                writeScenarioResult(scenario, jsonGenerator);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }

    private void writeFailedCriteria(List<FailedCriterion> failedCriteria, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeFieldName("failedCriteria");
        jsonGenerator.writeStartArray();
        for (FailedCriterion criterion : failedCriteria) {
            writeFailedCriterion(criterion, jsonGenerator);
        }
        jsonGenerator.writeEndArray();
    }

    private void writeFailedCriterion(FailedCriterion failedCriterion, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("description", failedCriterion.description());
        jsonGenerator.writeNumberField("time", failedCriterion.time());
        jsonGenerator.writeEndObject();
    }

    private void writeScenarioResult(ScenarioResult scenarioResult, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("id", scenarioResult.id());
        jsonGenerator.writeStringField("status", scenarioResult.status().toString());
        writeFailedCriteria(scenarioResult.failedCriteria(), jsonGenerator);
        jsonGenerator.writeEndObject();
    }
}
