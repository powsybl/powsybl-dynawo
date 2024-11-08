/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynaflow.DynaFlowConstants;
import com.powsybl.dynaflow.DynaFlowParameters;
import com.powsybl.dynaflow.DynaFlowSecurityAnalysisParameters;
import com.powsybl.loadflow.LoadFlowParameters;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Serializes parameters from {@link DynaFlowParameters} used by Dynawo.
 * Some parameters are directly used by powsybl-dynawo and thus not serialized.
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 */
public final class DynaFlowConfigSerializer {

    private DynaFlowConfigSerializer() {
    }

    public static void serialize(LoadFlowParameters lfParameters, DynaFlowParameters dynaFlowParameters,
                                 DynaFlowSecurityAnalysisParameters saParameters, Path workingDir, Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            JsonUtil.writeJson(writer, jsonGenerator -> serialize(lfParameters, dynaFlowParameters, saParameters, workingDir, jsonGenerator));
        }
    }

    public static void serialize(LoadFlowParameters lfParameters, DynaFlowParameters dynaFlowParameters, Path workingDir, Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            JsonUtil.writeJson(writer, jsonGenerator -> serialize(lfParameters, dynaFlowParameters, null, workingDir, jsonGenerator));
        }
    }

    public static void serialize(LoadFlowParameters lfParameters, DynaFlowParameters dynaFlowParameters, Path workingDir, Writer writer) {
        JsonUtil.writeJson(writer, jsonGenerator -> serialize(lfParameters, dynaFlowParameters, null, workingDir, jsonGenerator));
    }

    private static void serialize(LoadFlowParameters lfParameters, DynaFlowParameters dynaFlowParameters,
                                  DynaFlowSecurityAnalysisParameters saParameters, Path workingDir, JsonGenerator jsonGenerator) {
        try {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectFieldStart("dfl-config");
            serialize(lfParameters, jsonGenerator);
            serialize(dynaFlowParameters, jsonGenerator);
            if (saParameters != null) {
                serialize(saParameters, jsonGenerator);
            }
            jsonGenerator.writeStringField("OutputDir", workingDir.toString());
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void serialize(DynaFlowParameters dynaFlowParameters, JsonGenerator jsonGenerator) throws IOException {
        writeNonNullField(jsonGenerator, "SVCRegulationOn", dynaFlowParameters.getSvcRegulationOn());
        writeNonNullField(jsonGenerator, "ShuntRegulationOn", dynaFlowParameters.getShuntRegulationOn());
        writeNonNullField(jsonGenerator, "AutomaticSlackBusOn", dynaFlowParameters.getAutomaticSlackBusOn());
        writeNonNullField(jsonGenerator, "DsoVoltageLevel", dynaFlowParameters.getDsoVoltageLevel());

        if (dynaFlowParameters.getActivePowerCompensation() != null) {
            jsonGenerator.writeStringField("ActivePowerCompensation", dynaFlowParameters.getActivePowerCompensation().getDynaflowName());
        }
        writeNonNullField(jsonGenerator, "SettingPath", dynaFlowParameters.getSettingPath());
        writeNonNullField(jsonGenerator, "AssemblingPath", dynaFlowParameters.getAssemblingPath());
        writeNonNullField(jsonGenerator, "StartTime", dynaFlowParameters.getStartTime());
        writeNonNullField(jsonGenerator, "StopTime", dynaFlowParameters.getStopTime());
        writeNonNullField(jsonGenerator, "Precision", dynaFlowParameters.getPrecision());

        if (dynaFlowParameters.getChosenOutputs() != null) {
            jsonGenerator.writeFieldName("ChosenOutputs");
            jsonGenerator.writeStartArray();
            for (DynaFlowConstants.OutputTypes outputType : dynaFlowParameters.getChosenOutputs()) {
                jsonGenerator.writeString(outputType.name());
            }
            jsonGenerator.writeEndArray();
        }

        writeNonNullField(jsonGenerator, "TimeStep", dynaFlowParameters.getTimeStep());
        if (dynaFlowParameters.getStartingPointMode() != null) {
            jsonGenerator.writeStringField("StartingPointMode", dynaFlowParameters.getStartingPointMode().getName());
        }
    }

    private static void serialize(LoadFlowParameters lfParameters, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeBooleanField("InfiniteReactiveLimits", !lfParameters.isUseReactiveLimits());
    }

    private static void serialize(DynaFlowSecurityAnalysisParameters saParameters, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeObjectFieldStart("sa");
        jsonGenerator.writeNumberField("TimeOfEvent", saParameters.getContingenciesStartTime());
        jsonGenerator.writeEndObject();
    }

    private static void writeNonNullField(JsonGenerator jsonGenerator, String fieldName, Boolean value) throws IOException {
        if (value != null) {
            jsonGenerator.writeBooleanField(fieldName, value);
        }
    }

    private static void writeNonNullField(JsonGenerator jsonGenerator, String fieldName, Double value) throws IOException {
        if (value != null && !Double.isNaN(value)) {
            jsonGenerator.writeNumberField(fieldName, value);
        }
    }

    private static void writeNonNullField(JsonGenerator jsonGenerator, String fieldName, String value) throws IOException {
        if (value != null) {
            jsonGenerator.writeStringField(fieldName, value);
        }
    }
}
