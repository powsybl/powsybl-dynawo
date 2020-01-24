package com.powsybl.dynawo.simulator.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.auto.service.AutoService;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters.ExtensionSerializer;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.Solver;

@AutoService(ExtensionSerializer.class)
public class DynawoSimulationParametersSerializer implements JsonDynamicSimulationParameters.ExtensionSerializer<DynawoSimulationParameters> {

    @Override
    public String getCategoryName() {
        return "dynamic-simulation-parameters";
    }

    @Override
    public Class<? super DynawoSimulationParameters> getExtensionClass() {
        return DynawoSimulationParameters.class;
    }

    @Override
    public String getExtensionName() {
        return "DynawoSimulationParameters";
    }

    @Override
    public DynawoSimulationParameters deserialize(JsonParser parser, DeserializationContext deserializationContext)
        throws IOException {
        DynawoSimulationParameters parameters = new DynawoSimulationParameters();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentName()) {

                case "solver":
                    parser.nextToken();
                    parameters.setSolver(parser.readValueAs(Solver.class));
                    break;

                case "IDAorder":
                    parser.nextToken();
                    parameters.setIdaOrder(parser.readValueAs(Integer.class));
                    break;

                case "dslFile":
                    parser.nextToken();
                    parameters.setDslFilename(parser.readValueAs(String.class));
                    break;

                default:
                    throw new AssertionError("Unexpected field: " + parser.getCurrentName());
            }
        }
        return parameters;
    }

    @Override
    public void serialize(DynawoSimulationParameters dynawoSimulationParameters, JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider)
        throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeEndObject();
    }

}
