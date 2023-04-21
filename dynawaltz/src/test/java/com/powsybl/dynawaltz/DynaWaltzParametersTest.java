/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.test.AbstractConverterTest;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzParameters.SolverType;
import com.powsybl.dynawaltz.parameters.Parameter;
import com.powsybl.dynawaltz.parameters.ParameterType;
import com.powsybl.dynawaltz.parameters.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynaWaltzParametersTest extends AbstractConverterTest {

    public static final String USER_HOME = "/home/user/";

    private InMemoryPlatformConfig platformConfig;

    @BeforeEach
    public void setUp() throws IOException {
        super.setUp();
        platformConfig = new InMemoryPlatformConfig(fileSystem);
    }

    private void copyFile(String name, String parametersFile) throws IOException {
        Path path = platformConfig.getConfigDir()
                .map(cd -> cd.resolve(fileSystem.getPath(parametersFile)))
                .orElse(fileSystem.getPath(parametersFile));
        Objects.requireNonNull(getClass().getResourceAsStream(name))
                .transferTo(Files.newOutputStream(path));
    }

    @Test
    void checkParameters() throws IOException {
        String networkParametersId = "networkParametersId";
        SolverType solverType = SolverType.IDA;
        String solverParametersId = "solverParametersId";
        boolean mergeLoads = true;
        initPlatformConfig(networkParametersId, solverType, solverParametersId, mergeLoads);

        DynaWaltzParameters parameters = DynaWaltzParameters.load(platformConfig, fileSystem);

        checModelParameters(parameters);

        assertEquals(networkParametersId, parameters.getNetwork().getParametersId());
        Set networkParameters = parameters.getNetwork().getParametersSet().get(networkParametersId);
        Parameter loadTp = networkParameters.getParameter("load_Tp");
        assertEquals("90", loadTp.getValue());
        assertEquals("load_Tp", loadTp.getName());
        assertEquals(ParameterType.DOUBLE, loadTp.getType());
        Parameter loadControllable = networkParameters.getParameter("load_isControllable");
        assertEquals("false", loadControllable.getValue());
        assertEquals("load_isControllable", loadControllable.getName());
        assertEquals(ParameterType.BOOL, loadControllable.getType());

        DynaWaltzParameters.Solver solver = parameters.getSolver();
        assertEquals(solverParametersId, solver.getParametersId());
        Set solverParameters = solver.getParametersSet().get(solverParametersId);
        assertEquals(solverType, solver.getType());
        Parameter order = solverParameters.getParameter("order");
        assertEquals("1", order.getValue());
        assertEquals("order", order.getName());
        assertEquals(ParameterType.INT, order.getType());
        Parameter absAccuracy = solverParameters.getParameter("absAccuracy");
        assertEquals("1e-4", absAccuracy.getValue());
        assertEquals("absAccuracy", absAccuracy.getName());
        assertEquals(ParameterType.DOUBLE, absAccuracy.getType());

        assertEquals(mergeLoads, parameters.isMergeLoads());
    }

    @Test
    void roundTripParametersSerializing() throws IOException {
        String networkParametersId = "networkParametersId";
        SolverType solverType = SolverType.IDA;
        String solverParametersId = "solverParametersId";
        boolean mergeLoads = false;
        initPlatformConfig(networkParametersId, solverType, solverParametersId, mergeLoads);

        DynamicSimulationParameters dynamicSimulationParameters = new DynamicSimulationParameters()
                .setStartTime(0)
                .setStopTime(3600);
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load(platformConfig);
        dynamicSimulationParameters.addExtension(DynaWaltzParameters.class, dynawoParameters);
        roundTripTest(dynamicSimulationParameters, JsonDynamicSimulationParameters::write,
                JsonDynamicSimulationParameters::read, "/DynaWaltzParameters.json");
    }

    private void initPlatformConfig(String networkParametersId, SolverType solverType, String solverParametersId, boolean mergeLoads) throws IOException {
        String parametersFile = USER_HOME + "parametersFile";
        String networkParametersFile = USER_HOME + "networkParametersFile";
        String solverParametersFile = USER_HOME + "solverParametersFile";

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawaltz-default-parameters");
        moduleConfig.setStringProperty("parametersFile", parametersFile);
        moduleConfig.setStringProperty("network.parametersFile", networkParametersFile);
        moduleConfig.setStringProperty("solver.parametersFile", solverParametersFile);
        moduleConfig.setStringProperty("mergeLoads", String.valueOf(mergeLoads));
        moduleConfig.setStringProperty("network.parametersId", networkParametersId);
        moduleConfig.setStringProperty("solver.type", solverType.toString());
        moduleConfig.setStringProperty("solver.parametersId", solverParametersId);

        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", parametersFile);
        copyFile("/parametersSet/network.par", networkParametersFile);
        copyFile("/parametersSet/solvers.par", solverParametersFile);
    }

    @Test
    void checkDefaultParameters() throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DynaWaltzParameters.DEFAULT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynaWaltzParameters.DEFAULT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solvers.par", DynaWaltzParameters.DEFAULT_SOLVER_PARAMETERS_FILE);

        DynaWaltzParameters parameters = DynaWaltzParameters.load(platformConfig, fileSystem);
        checModelParameters(parameters);
//        assertEquals(networkParametersFile, parameters.getNetwork().getParametersFile()); FIXME: should be empty
        assertEquals(DynaWaltzParameters.DEFAULT_NETWORK_PAR_ID, parameters.getNetwork().getParametersId());
        assertEquals(DynaWaltzParameters.DEFAULT_SOLVER_TYPE, parameters.getSolver().getType());
//        assertEquals(solverParametersFile, parameters.getSolver().getParametersFile()); FIXME: should be empty
        assertEquals(DynaWaltzParameters.DEFAULT_SOLVER_PAR_ID, parameters.getSolver().getParametersId());
        assertEquals(DynaWaltzParameters.DEFAULT_MERGE_LOADS, parameters.isMergeLoads());
    }

    private static void checModelParameters(DynaWaltzParameters dynaWaltzParameters) {
        Parameter booleanParameter = dynaWaltzParameters.getModelParameterSet("test").getParameter("boolean");
        assertEquals("true", booleanParameter.getValue());
        assertEquals("boolean", booleanParameter.getName());
        assertEquals(ParameterType.BOOL, booleanParameter.getType());
        Parameter stringParameter = dynaWaltzParameters.getModelParameterSet("test").getParameter("string");
        assertEquals("aString", stringParameter.getValue());
        assertEquals("string", stringParameter.getName());
        assertEquals(ParameterType.STRING, stringParameter.getType());
    }
}
