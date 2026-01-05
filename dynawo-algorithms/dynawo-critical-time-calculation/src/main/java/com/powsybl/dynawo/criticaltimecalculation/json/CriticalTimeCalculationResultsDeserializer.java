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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynawo.criticaltimecalculation.results.Status;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResult;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResults;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationResultsDeserializer extends StdDeserializer<CriticalTimeCalculationResults> {

    CriticalTimeCalculationResultsDeserializer() {
        super(CriticalTimeCalculationResults.class);
    }

    public static CriticalTimeCalculationResults read(InputStream is) throws IOException {
        Objects.requireNonNull(is);
        ObjectMapper objectMapper = JsonUtil.createObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CriticalTimeCalculationResults.class, new CriticalTimeCalculationResultsDeserializer());
        objectMapper.registerModule(module);
        return objectMapper.readValue(is, CriticalTimeCalculationResults.class);
    }

    public static CriticalTimeCalculationResults read(Path jsonFile) {
        Objects.requireNonNull(jsonFile);
        try (InputStream is = Files.newInputStream(jsonFile)) {
            return read(is);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public CriticalTimeCalculationResults deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        List<CriticalTimeCalculationResult> results = new ArrayList<>();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.currentName()) {
                case "version" -> parser.nextToken(); // skip
                case "scenarioResults" -> {
                    parser.nextToken();
                    deserializeCriticalTimeCalculationResult(parser, results);
                }
                default -> throw getUnexpectedFieldException(parser);
            }
        }
        return new CriticalTimeCalculationResults(results);
    }

    private void deserializeCriticalTimeCalculationResult(JsonParser parser, List<CriticalTimeCalculationResult> scenarioResults) throws IOException {
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            String id = null;
            Status status = null;
            double criticalTime = Double.NaN;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                switch (parser.currentName()) {
                    case "id" -> id = parser.getValueAsString();
                    case "status" -> {
                        parser.nextToken();
                        status = parser.readValueAs(Status.class);
                    }
                    case "criticalTime" -> {
                        parser.nextToken();
                        parser.getValueAsDouble();
                    }
                    default -> throw getUnexpectedFieldException(parser);
                }
            }
            scenarioResults.add(new CriticalTimeCalculationResult(id, status, criticalTime));
        }
    }

    private static IllegalStateException getUnexpectedFieldException(JsonParser parser) throws IOException {
        return new IllegalStateException("Unexpected field: " + parser.currentName());
    }
}
