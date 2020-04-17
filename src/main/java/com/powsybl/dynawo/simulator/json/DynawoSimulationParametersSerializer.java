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
        // TODO: use "dynamic-simulation-parameters" when powsybl-core v3.3.0 is
        // released
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
        Network network = new Network();
        Solver solver = new Solver();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentName()) {

                case "parametersFile":
                    parser.nextToken();
                    parametersFile = parser.readValueAs(String.class);
                    break;

                case "network":
                    parser.nextToken();
                    deserializeNetwork(parser, network);
                    break;

                case "solver":
                    parser.nextToken();
                    deserializeSolver(parser, solver);
                    break;

                default:
                    throw new AssertionError("Unexpected field: " + parser.getCurrentName());
            }
        }

        return new DynawoSimulationParameters(parametersFile, network.getParametersId(), solver.getType(), solver.getParametersFile(),
            solver.getParametersId());
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

    private void deserializeNetwork(JsonParser parser, Network network)
        throws IOException {
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentName()) {

                case "parametersId":
                    parser.nextToken();
                    network.setParametersId(parser.readValueAs(String.class));
                    break;

                default:
                    throw new AssertionError("Unexpected field: " + parser.getCurrentName());
            }
        }
    }

    private void deserializeSolver(JsonParser parser, Solver solver)
        throws IOException {
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentName()) {

                case "type":
                    parser.nextToken();
                    solver.setType(parser.readValueAs(SolverType.class));
                    break;

                case "parametersFile":
                    parser.nextToken();
                    solver.setParametersFile(parser.readValueAs(String.class));
                    break;

                case "parametersId":
                    parser.nextToken();
                    solver.setParametersId(parser.readValueAs(String.class));
                    break;

                default:
                    throw new AssertionError("Unexpected field: " + parser.getCurrentName());
            }
        }
    }

    class Network {

        Network() {
            parametersId = DynawoSimulationParameters.DEFAULT_NETWORK_PAR_ID;
        }

        void setParametersId(String parametersId) {
            this.parametersId = parametersId;
        }

        String getParametersId() {
            return parametersId;
        }

        private String parametersId;
    }

    class Solver {

        Solver() {
            type = DynawoSimulationParameters.DEFAULT_SOLVER_TYPE;
            parametersFile = null;
            parametersId = DynawoSimulationParameters.DEFAULT_SOLVER_PAR_ID;
        }

        void setType(SolverType type) {
            this.type = type;
        }

        SolverType getType() {
            return type;
        }

        void setParametersFile(String parametersFile) {
            this.parametersFile = parametersFile;
        }

        String getParametersFile() {
            return parametersFile;
        }

        void setParametersId(String parametersId) {
            this.parametersId = parametersId;
        }

        String getParametersId() {
            return parametersId;
        }

        private SolverType type;
        private String parametersFile;
        private String parametersId;
    }
}
