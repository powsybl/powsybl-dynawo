/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverType;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(ExtensionSerializer.class)
public class DynawoSimulationParametersSerializer implements JsonDynamicSimulationParameters.ExtensionSerializer<DynawoSimulationParameters> {

    @Override
    public String getCategoryName() {
        // TODO: use "dynamic-simulation-parameters" when powsybl-core v3.3.0 is released
        return "DynamicSimulation-parameters";
    }

    @Override
    public Class<? super DynawoSimulationParameters> getExtensionClass() {
        return DynawoSimulationParameters.class;
    }

    @Override
    public String getExtensionName() {
        return "DynawoParameters";
    }

    @Override
    public DynawoSimulationParameters deserialize(JsonParser parser, DeserializationContext deserializationContext)
        throws IOException {

        String parametersFile = null;
        String networkParametersId = DynawoSimulationParameters.DEFAULT_NETWORK_PAR_ID;
        SolverType solverType = DynawoSimulationParameters.DEFAULT_SOLVER_TYPE;
        String solverParametersFile = null;
        String solverParametersId = DynawoSimulationParameters.DEFAULT_SOLVER_PAR_ID;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentName()) {

                case "parametersFile":
                    parser.nextToken();
                    parametersFile = parser.readValueAs(String.class);
                    break;

                case "network":
                    parser.nextToken();
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        switch (parser.getCurrentName()) {

                            case "parametersId":
                                parser.nextToken();
                                networkParametersId = parser.readValueAs(String.class);
                                break;

                            default:
                                throw new AssertionError("Unexpected field: " + parser.getCurrentName());
                        }
                    }
                    break;

                case "solver":
                    parser.nextToken();
                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                        switch (parser.getCurrentName()) {

                            case "type":
                                parser.nextToken();
                                solverType = parser.readValueAs(SolverType.class);
                                break;

                            case "parametersFile":
                                parser.nextToken();
                                solverParametersFile = parser.readValueAs(String.class);
                                break;

                            case "parametersId":
                                parser.nextToken();
                                solverParametersId = parser.readValueAs(String.class);
                                break;

                            default:
                                throw new AssertionError("Unexpected field: " + parser.getCurrentName());
                        }
                    }
                    break;

                default:
                    throw new AssertionError("Unexpected field: " + parser.getCurrentName());
            }
        }

        return new DynawoSimulationParameters(parametersFile, networkParametersId, solverType, solverParametersFile, solverParametersId);
    }

    @Override
    public void serialize(DynawoSimulationParameters dynawoSimulationParameters, JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("parametersFile", dynawoSimulationParameters.getParametersFile());

        jsonGenerator.writeFieldName("network");
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("parametersId", dynawoSimulationParameters.getNetworkParametersId());
        jsonGenerator.writeEndObject();

        jsonGenerator.writeFieldName("solver");
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("type", dynawoSimulationParameters.getSolverType().toString());
        jsonGenerator.writeStringField("parametersFile", dynawoSimulationParameters.getSolverParametersFile());
        jsonGenerator.writeStringField("parametersId", dynawoSimulationParameters.getSolverParametersId());
        jsonGenerator.writeEndObject();

        jsonGenerator.writeEndObject();
    }

}
