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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynaflow.results.FailedCriterion;
import com.powsybl.dynaflow.results.ScenarioResult;
import com.powsybl.dynaflow.results.Status;
import com.powsybl.dynawo.margincalculation.results.LoadIncreaseResult;
import com.powsybl.dynawo.margincalculation.results.MarginCalculationResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class MarginCalculationResultDeserializer extends StdDeserializer<MarginCalculationResult> {

    MarginCalculationResultDeserializer() {
        super(MarginCalculationResult.class);
    }

    public static MarginCalculationResult read(InputStream is) throws IOException {
        Objects.requireNonNull(is);
        ObjectMapper objectMapper = JsonUtil.createObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(MarginCalculationResult.class, new MarginCalculationResultDeserializer());
        objectMapper.registerModule(module);
        return objectMapper.readValue(is, MarginCalculationResult.class);
    }

    public static MarginCalculationResult read(Path jsonFile) {
        Objects.requireNonNull(jsonFile);
        try (InputStream is = Files.newInputStream(jsonFile)) {
            return read(is);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public MarginCalculationResult deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
        List<LoadIncreaseResult> loadIncreaseResults = new ArrayList<>();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.currentName()) {
                case "version" -> parser.nextToken(); // skip
                case "loadIncreases" -> {
                    parser.nextToken();
                    deserializeLoadIncreaseResults(parser, loadIncreaseResults);
                }
                default -> throw getUnexpectedFieldException(parser);
            }
        }
        return new MarginCalculationResult(loadIncreaseResults);
    }

    private void deserializeLoadIncreaseResults(JsonParser parser, List<LoadIncreaseResult> loadIncreaseResults) throws IOException {
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            double loadLevel = 0;
            Status status = null;
            List<FailedCriterion> failedCriteria = new ArrayList<>();
            List<ScenarioResult> scenarioResults = new ArrayList<>();
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                switch (parser.currentName()) {
                    case "loadLevel" -> loadLevel = parser.getValueAsDouble();
                    case "status" -> {
                        parser.nextToken();
                        status = parser.readValueAs(Status.class);
                    }
                    case "failedCriteria" -> {
                        parser.nextToken();
                        deserializeFailedCriteria(parser, failedCriteria);
                    }
                    case "scenarioResults" -> {
                        parser.nextToken();
                        deserializeScenarioResults(parser, scenarioResults);
                    }
                    default -> throw getUnexpectedFieldException(parser);
                }
            }
            loadIncreaseResults.add(new LoadIncreaseResult(loadLevel, status, scenarioResults, failedCriteria));
        }
    }

    private void deserializeFailedCriteria(JsonParser parser, List<FailedCriterion> failedCriteria) throws IOException {
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            String description = null;
            double time = 0;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                switch (parser.currentName()) {
                    case "description" -> description = parser.getValueAsString();
                    case "time" -> time = parser.getValueAsDouble();
                    default -> throw getUnexpectedFieldException(parser);
                }
            }
            failedCriteria.add(new FailedCriterion(description, time));
        }
    }

    private void deserializeScenarioResults(JsonParser parser, List<ScenarioResult> scenarioResults) throws IOException {
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            String id = null;
            Status status = null;
            List<FailedCriterion> failedCriteria = new ArrayList<>();
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                switch (parser.currentName()) {
                    case "id" -> id = parser.getValueAsString();
                    case "status" -> {
                        parser.nextToken();
                        status = parser.readValueAs(Status.class);
                    }
                    case "failedCriteria" -> {
                        parser.nextToken();
                        deserializeFailedCriteria(parser, failedCriteria);
                    }
                    default -> throw getUnexpectedFieldException(parser);
                }
            }
            scenarioResults.add(new ScenarioResult(id, status, failedCriteria));
        }
    }

    private static IllegalStateException getUnexpectedFieldException(JsonParser parser) throws IOException {
        return new IllegalStateException("Unexpected field: " + parser.currentName());
    }
}
