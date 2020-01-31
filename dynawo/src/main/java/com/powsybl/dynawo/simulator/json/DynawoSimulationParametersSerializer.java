/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
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
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverIDAParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverType;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(ExtensionSerializer.class)
public class DynawoSimulationParametersSerializer
    implements JsonDynamicSimulationParameters.ExtensionSerializer<DynawoSimulationParameters> {

    @Override
    public String getCategoryName() {
        return "DynamicSimulation-parameters";
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
        SolverType solverType = DynawoSimulationParameters.DEFAULT_SOLVER_TYPE;
        int idaOrder = DynawoSimulationParameters.DEFAULT_IDA_ORDER;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentName()) {

                case "solver":
                    parser.nextToken();
                    solverType = parser.readValueAs(SolverType.class);
                    break;

                case "IDAorder":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.IDA) {
                        idaOrder = parser.readValueAs(Integer.class);
                    } else {
                        throw new AssertionError("The IDAorder parameter is used only with IDA solver");
                    }
                    break;

                case "dslFile":
                    parser.nextToken();
                    parameters.setDslFilename(parser.readValueAs(String.class));
                    break;

                default:
                    throw new AssertionError("Unexpected field: " + parser.getCurrentName());
            }
        }
        if (solverType == SolverType.IDA) {
            parameters.setSolverParameters(new SolverIDAParameters(idaOrder));
        } else {
            parameters.setSolverParameters(new SolverParameters(solverType));
        }
        return parameters;
    }

    @Override
    public void serialize(DynawoSimulationParameters dynawoSimulationParameters, JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider)
        throws IOException {
        jsonGenerator.writeStartObject();

        SolverParameters solverParameters = dynawoSimulationParameters.getSolverParameters();
        jsonGenerator.writeStringField("solver", solverParameters.getType().toString());
        if (solverParameters.getType() == SolverType.IDA) {
            jsonGenerator.writeNumberField("IDAorder", ((SolverIDAParameters) solverParameters).getOrder());
        }
        jsonGenerator.writeStringField("dslFile", dynawoSimulationParameters.getDslFilename());

        jsonGenerator.writeEndObject();
    }

}
