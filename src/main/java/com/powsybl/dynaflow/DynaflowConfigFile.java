package com.powsybl.dynaflow;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.powsybl.loadflow.LoadFlowParameters;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.powsybl.dynaflow.DynaflowConstants.CONFIG_FILENAME;

public final class DynaflowConfigFile {
    private DynaflowConfigFile() {

    }

    static void writeDynaflowConfigInputFile(DynaflowParameters parameters, Path workingDir, LoadFlowParameters params) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(workingDir.resolve(CONFIG_FILENAME))) {
            writeDynaflowConfigInputFile(parameters, outputStream, params);
        }
    }

    static void writeDynaflowConfigInputFile(DynaflowParameters parameters, OutputStream outputStream, LoadFlowParameters params) throws IOException {
        JsonFactory factory = new JsonFactory();
        try (JsonGenerator jsonGenerator = factory.createGenerator(outputStream, JsonEncoding.UTF8)) {
            jsonGenerator.useDefaultPrettyPrinter();
            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectFieldStart("dfl-config");
            jsonGenerator.writeBooleanField("SVCRegulationOn", parameters.getSvcRegulationOn());
            jsonGenerator.writeBooleanField("ShuntRegulationOn", parameters.getShuntRegulationOn());
            jsonGenerator.writeBooleanField("AutomaticSlackBusOn", parameters.getAutomaticSlackBusOn());
            jsonGenerator.writeBooleanField("VSCAsGenerators", parameters.getVscAsGenerators());
            jsonGenerator.writeBooleanField("LCCAsLoads", parameters.getLccAsLoads());
            jsonGenerator.writeBooleanField("InfiniteReactiveLimits", params.isNoGeneratorReactiveLimits());
            jsonGenerator.writeBooleanField("PSTRegulationOn", params.isPhaseShifterRegulationOn());
            jsonGenerator.writeEndObject();
            jsonGenerator.writeEndObject();
        }

    }
}
