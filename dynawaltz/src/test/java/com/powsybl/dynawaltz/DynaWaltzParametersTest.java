/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.test.AbstractConverterTest;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzParameters.SolverType;
import com.powsybl.dynawaltz.parameters.Parameter;
import com.powsybl.dynawaltz.parameters.ParameterType;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.dynawaltz.xml.ParametersXml;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
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

        checkModelParameters(parameters);

        assertEquals(networkParametersId, parameters.getNetworkParameters().getId());
        ParametersSet networkParameters = parameters.getNetworkParameters();
        Parameter loadTp = networkParameters.getParameter("load_Tp");
        assertEquals("90", loadTp.value());
        assertEquals("load_Tp", loadTp.name());
        assertEquals(ParameterType.DOUBLE, loadTp.type());
        Parameter loadControllable = networkParameters.getParameter("load_isControllable");
        assertEquals("false", loadControllable.value());
        assertEquals("load_isControllable", loadControllable.name());
        assertEquals(ParameterType.BOOL, loadControllable.type());

        ParametersSet solverParameters = parameters.getSolverParameters();
        assertEquals(solverParametersId, solverParameters.getId());
        assertEquals(solverType, parameters.getSolverType());
        Parameter order = solverParameters.getParameter("order");
        assertEquals("1", order.value());
        assertEquals("order", order.name());
        assertEquals(ParameterType.INT, order.type());
        Parameter absAccuracy = solverParameters.getParameter("absAccuracy");
        assertEquals("1e-4", absAccuracy.value());
        assertEquals("absAccuracy", absAccuracy.name());
        assertEquals(ParameterType.DOUBLE, absAccuracy.type());

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
        copyFile("/parametersSet/models.par", DynaWaltzParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynaWaltzParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solvers.par", DynaWaltzParameters.DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);

        DynaWaltzParameters parameters = DynaWaltzParameters.load(platformConfig, fileSystem);
        checkModelParameters(parameters);

        assertEquals(DynaWaltzParameters.DEFAULT_NETWORK_PAR_ID, parameters.getNetworkParameters().getId());
        assertTrue(parameters.getNetworkParameters().getParameters().isEmpty());
        assertTrue(parameters.getNetworkParameters().getReferences().isEmpty());

        assertEquals(DynaWaltzParameters.DEFAULT_SOLVER_TYPE, parameters.getSolverType());
        assertEquals(DynaWaltzParameters.DEFAULT_SOLVER_PAR_ID, parameters.getSolverParameters().getId());
        assertEquals("1", parameters.getSolverParameters().getId());

        assertEquals(DynaWaltzParameters.DEFAULT_MERGE_LOADS, parameters.isMergeLoads());
    }

    @Test
    void checkException() throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DynaWaltzParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynaWaltzParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solversMissingDefault.par", DynaWaltzParameters.DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);

        PowsyblException e1 = assertThrows(PowsyblException.class, () -> DynaWaltzParameters.load(platformConfig, fileSystem));
        assertEquals("Could not find parameters set with id='1' in file '/work/inmemory/solvers.par'", e1.getMessage());

        try (InputStream is = getClass().getResourceAsStream("/parametersSet/solvers.par")) {
            PowsyblException e2 = assertThrows(PowsyblException.class, () -> ParametersXml.load(is, "2"));
            assertEquals("Could not find parameters set with id='2' in given input stream", e2.getMessage());
        }
    }

    private static void checkModelParameters(DynaWaltzParameters dynaWaltzParameters) {
        Parameter booleanParameter = dynaWaltzParameters.getModelParameters("test").getParameter("boolean");
        assertEquals("true", booleanParameter.value());
        assertEquals("boolean", booleanParameter.name());
        assertEquals(ParameterType.BOOL, booleanParameter.type());
        Parameter stringParameter = dynaWaltzParameters.getModelParameters("test").getParameter("string");
        assertEquals("aString", stringParameter.value());
        assertEquals("string", stringParameter.name());
        assertEquals(ParameterType.STRING, stringParameter.type());
    }
}
