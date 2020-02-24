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
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverSIMParameters;
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
        double idaInitStep = DynawoSimulationParameters.DEFAULT_IDA_MIN_STEP;
        double idaMinStep = DynawoSimulationParameters.DEFAULT_IDA_MIN_STEP;
        double idaMaxStep = DynawoSimulationParameters.DEFAULT_IDA_MAX_STEP;
        double idaRelAccuracy = DynawoSimulationParameters.DEFAULT_IDA_ACCURACY;
        double idaAbsAccuracy = DynawoSimulationParameters.DEFAULT_IDA_ACCURACY;
        double simHMin = DynawoSimulationParameters.DEFAULT_SIM_H_MIN;
        double simHMax = DynawoSimulationParameters.DEFAULT_SIM_H_MAX;
        double simKReduceStep = DynawoSimulationParameters.DEFAULT_SIM_K_REDUCE_STEP;
        int simNEff = DynawoSimulationParameters.DEFAULT_SIM_N_EFF;
        int simNDeadband = DynawoSimulationParameters.DEFAULT_SIM_N_DEADBAND;
        int simMaxRootRestart = DynawoSimulationParameters.DEFAULT_SIM_MAX_ROOT_RESTART;
        int simMaxNewtonTry = DynawoSimulationParameters.DEFAULT_SIM_MAX_NEWTON_TRY;
        String simLnearSolverName = DynawoSimulationParameters.DEFAULT_SIM_LINEAR_SOLVER_NAME;
        boolean simRecalculateStep = DynawoSimulationParameters.DEFAULT_SIM_RECALCULATE_STEP;
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

                case "IDAinitStep":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.IDA) {
                        idaInitStep = parser.readValueAs(Double.class);
                    } else {
                        throw new AssertionError("The IDAinitStep parameter is used only with IDA solver");
                    }
                    break;

                case "IDAminStep":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.IDA) {
                        idaMinStep = parser.readValueAs(Double.class);
                    } else {
                        throw new AssertionError("The IDAminStep parameter is used only with IDA solver");
                    }
                    break;

                case "IDAmaxStep":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.IDA) {
                        idaMaxStep = parser.readValueAs(Double.class);
                    } else {
                        throw new AssertionError("The IDAmaxStep parameter is used only with IDA solver");
                    }
                    break;

                case "IDArelAccuracy":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.IDA) {
                        idaRelAccuracy = parser.readValueAs(Double.class);
                    } else {
                        throw new AssertionError("The IDArelAccuracy parameter is used only with IDA solver");
                    }
                    break;

                case "IDAabsAccuracy":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.IDA) {
                        idaAbsAccuracy = parser.readValueAs(Double.class);
                    } else {
                        throw new AssertionError("The IDAabsAccuracy parameter is used only with IDA solver");
                    }
                    break;

                case "SIMhMin":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.SIM) {
                        simHMin = parser.readValueAs(Double.class);
                    } else {
                        throw new AssertionError("The SIMhMin parameter is used only with SIM solver");
                    }
                    break;

                case "SIMhMax":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.SIM) {
                        simHMax = parser.readValueAs(Double.class);
                    } else {
                        throw new AssertionError("The SIMhMin parameter is used only with SIM solver");
                    }
                    break;

                case "SIMkReduceStep":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.SIM) {
                        simKReduceStep = parser.readValueAs(Double.class);
                    } else {
                        throw new AssertionError("The SIMhMin parameter is used only with SIM solver");
                    }
                    break;

                case "SIMnEff":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.SIM) {
                        simNEff = parser.readValueAs(Integer.class);
                    } else {
                        throw new AssertionError("The SIMnEff parameter is used only with SIM solver");
                    }
                    break;

                case "SIMnDeadband":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.SIM) {
                        simNDeadband = parser.readValueAs(Integer.class);
                    } else {
                        throw new AssertionError("The SIMnDeadband parameter is used only with SIM solver");
                    }
                    break;

                case "SIMmaxRootRestart":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.SIM) {
                        simMaxRootRestart = parser.readValueAs(Integer.class);
                    } else {
                        throw new AssertionError("The SIMmaxRootRestart parameter is used only with SIM solver");
                    }
                    break;

                case "SIMmaxNewtonTry":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.SIM) {
                        simMaxNewtonTry = parser.readValueAs(Integer.class);
                    } else {
                        throw new AssertionError("The SIMmaxNewtonTry parameter is used only with SIM solver");
                    }
                    break;

                case "SIMlinearSolverName":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.SIM) {
                        simLnearSolverName = parser.readValueAs(String.class);
                    } else {
                        throw new AssertionError("The SIMlinearSolverName parameter is used only with SIM solver");
                    }
                    break;

                case "SIMrecalculateStep":
                    parser.nextToken();
                    // We assume that the parameters are in the correct order
                    if (solverType == SolverType.SIM) {
                        simRecalculateStep = parser.readValueAs(Boolean.class);
                    } else {
                        throw new AssertionError("The SIMrecalculateStep parameter is used only with SIM solver");
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
            parameters.setSolverParameters(
                new SolverIDAParameters(idaOrder, idaInitStep, idaMinStep, idaMaxStep, idaRelAccuracy, idaAbsAccuracy));
        } else if (solverType == SolverType.SIM) {
            parameters.setSolverParameters(
                new SolverSIMParameters(simHMin, simHMax, simKReduceStep, simNEff, simNDeadband, simMaxRootRestart,
                    simMaxNewtonTry, simLnearSolverName, simRecalculateStep));
        } else {
            parameters.setSolverParameters(DynawoSimulationParameters.DEFAULT_SOLVER_PARAMETERS);
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
            SolverIDAParameters solverIDAParameters = (SolverIDAParameters) solverParameters;
            jsonGenerator.writeNumberField("IDAorder", solverIDAParameters.getOrder());
            jsonGenerator.writeNumberField("IDAinitStep", solverIDAParameters.getInitStep());
            jsonGenerator.writeNumberField("IDAminStep", solverIDAParameters.getMinStep());
            jsonGenerator.writeNumberField("IDAmaxStep", solverIDAParameters.getMaxStep());
            jsonGenerator.writeNumberField("IDArelAccuracy", solverIDAParameters.getRelAccuracy());
            jsonGenerator.writeNumberField("IDAabsAccuracy", solverIDAParameters.getAbsAccuracy());
        } else if (solverParameters.getType() == SolverType.SIM) {
            SolverSIMParameters solverSIMParameters = (SolverSIMParameters) solverParameters;
            jsonGenerator.writeNumberField("SIMhMin", solverSIMParameters.gethMin());
            jsonGenerator.writeNumberField("SIMhMax", solverSIMParameters.gethMax());
            jsonGenerator.writeNumberField("SIMkReduceStep", solverSIMParameters.getkReduceStep());
            jsonGenerator.writeNumberField("SIMnEff", solverSIMParameters.getnEff());
            jsonGenerator.writeNumberField("SIMnDeadband", solverSIMParameters.getnDeadBand());
            jsonGenerator.writeNumberField("SIMmaxRootRestart", solverSIMParameters.getMaxRootRestart());
            jsonGenerator.writeNumberField("SIMmaxNewtonTry", solverSIMParameters.getMaxNewtonTry());
            jsonGenerator.writeStringField("SIMlinearSolverName", solverSIMParameters.getLinearSolverName());
            jsonGenerator.writeBooleanField("SIMrecalculateStep", solverSIMParameters.recalculateStep());
        }
        jsonGenerator.writeStringField("dslFile", dynawoSimulationParameters.getDslFilename());

        jsonGenerator.writeEndObject();
    }

}
