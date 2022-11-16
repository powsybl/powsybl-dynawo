/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynaflow.DynaFlowParameters;
import com.powsybl.loadflow.LoadFlowParameters;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public final class DynaFlowConfigSerializer {

    private DynaFlowConfigSerializer() {
    }

    public static void serialize(LoadFlowParameters lfParameters, DynaFlowParameters dynaFlowParameters, Path workingDir, Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            JsonUtil.writeJson(writer, jsonGenerator -> serialize(lfParameters, dynaFlowParameters, workingDir, jsonGenerator));
        }
    }

    public static void serialize(LoadFlowParameters lfParameters, DynaFlowParameters dynaFlowParameters, Path workingDir, Writer writer) {
        JsonUtil.writeJson(writer, jsonGenerator -> serialize(lfParameters, dynaFlowParameters, workingDir, jsonGenerator));
    }

    private static void serialize(LoadFlowParameters lfParameters, DynaFlowParameters dynaFlowParameters, Path workingDir, JsonGenerator jsonGenerator) {
        try {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectFieldStart("dfl-config");
            if (dynaFlowParameters.getSvcRegulationOn() != null) {
                jsonGenerator.writeBooleanField("SVCRegulationOn", dynaFlowParameters.getSvcRegulationOn());
            }
            if (dynaFlowParameters.getShuntRegulationOn() != null) {
                jsonGenerator.writeBooleanField("ShuntRegulationOn", dynaFlowParameters.getShuntRegulationOn());
            }
            if (dynaFlowParameters.getAutomaticSlackBusOn() != null) {
                jsonGenerator.writeBooleanField("AutomaticSlackBusOn", dynaFlowParameters.getAutomaticSlackBusOn());
            }
            if (dynaFlowParameters.getDsoVoltageLevel() != null) {
                jsonGenerator.writeNumberField("DsoVoltageLevel", dynaFlowParameters.getDsoVoltageLevel());
            }
            jsonGenerator.writeBooleanField("InfiniteReactiveLimits", lfParameters.isNoGeneratorReactiveLimits());
            if (dynaFlowParameters.getActivePowerCompensation() != null) {
                jsonGenerator.writeStringField("ActivePowerCompensation", dynaFlowParameters.getActivePowerCompensation().name());
            }
            if (dynaFlowParameters.getSettingPath() != null) {
                jsonGenerator.writeStringField("SettingPath", dynaFlowParameters.getSettingPath());
            }
            if (dynaFlowParameters.getAssemblingPath() != null) {
                jsonGenerator.writeStringField("AssemblingPath", dynaFlowParameters.getAssemblingPath());
            }
            if (dynaFlowParameters.getStartTime() != null) {
                jsonGenerator.writeNumberField("StartTime", dynaFlowParameters.getStartTime());
            }
            if (dynaFlowParameters.getStopTime() != null) {
                jsonGenerator.writeNumberField("StopTime", dynaFlowParameters.getStopTime());
            }
            if (dynaFlowParameters.getPrecision() != null) {
                jsonGenerator.writeNumberField("Precision", dynaFlowParameters.getPrecision());
            }
            if (dynaFlowParameters.getSa() != null) {
                DynaFlowParameters.Sa.writeJson(jsonGenerator, dynaFlowParameters);
            }
            if (dynaFlowParameters.getChosenOutputs() != null) {
                jsonGenerator.writeFieldName("ChosenOutputs");
                jsonGenerator.writeStartArray();
                for (String outputType : dynaFlowParameters.getChosenOutputs()) {
                    jsonGenerator.writeString(outputType);
                }
                jsonGenerator.writeEndArray();
            }
            if (dynaFlowParameters.getTimeStep() != null) {
                jsonGenerator.writeNumberField("TimeStep", dynaFlowParameters.getTimeStep());
            }
            jsonGenerator.writeStringField("OutputDir", workingDir.toAbsolutePath().toString());
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
