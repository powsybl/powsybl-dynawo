/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.dynaflow.DynaflowParameters;
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
public final class DynaflowConfigSerializer {

    private DynaflowConfigSerializer() {
    }

    public static void serialize(LoadFlowParameters lfParameters, DynaflowParameters dynaflowParameters, Path workingDir, Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            JsonUtil.writeJson(writer, jsonGenerator -> serialize(lfParameters, dynaflowParameters, workingDir, jsonGenerator));
        }
    }

    public static void serialize(LoadFlowParameters lfParameters, DynaflowParameters dynaflowParameters, Path workingDir, Writer writer) {
        JsonUtil.writeJson(writer, jsonGenerator -> serialize(lfParameters, dynaflowParameters, workingDir, jsonGenerator));
    }

    private static void serialize(LoadFlowParameters lfParameters, DynaflowParameters dynaflowParameters, Path workingDir, JsonGenerator jsonGenerator) {
        try {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectFieldStart("dfl-config");
            jsonGenerator.writeBooleanField("SVCRegulationOn", dynaflowParameters.getSvcRegulationOn());
            jsonGenerator.writeBooleanField("ShuntRegulationOn", dynaflowParameters.getShuntRegulationOn());
            jsonGenerator.writeBooleanField("AutomaticSlackBusOn", dynaflowParameters.getAutomaticSlackBusOn());
            jsonGenerator.writeBooleanField("VSCAsGenerators", dynaflowParameters.getVscAsGenerators());
            jsonGenerator.writeBooleanField("LCCAsLoads", dynaflowParameters.getLccAsLoads());
            jsonGenerator.writeNumberField("DsoVoltageLevel", dynaflowParameters.getDsoVoltageLevel());
            jsonGenerator.writeBooleanField("InfiniteReactiveLimits", lfParameters.isNoGeneratorReactiveLimits());
            jsonGenerator.writeBooleanField("PSTRegulationOn", lfParameters.isPhaseShifterRegulationOn());
            jsonGenerator.writeStringField("OutputDir", workingDir.toAbsolutePath().toString());
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
