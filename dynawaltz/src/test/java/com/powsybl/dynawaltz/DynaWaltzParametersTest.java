/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.dynawaltz.DynaWaltzParameters.SolverType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynaWaltzParametersTest {

    public static final String USER_HOME = "/home/user/";

    private InMemoryPlatformConfig platformConfig;
    private FileSystem fileSystem;

    @BeforeEach
    void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
    }

    private void copyFile(String name, String parametersFile) throws IOException {
        Objects.requireNonNull(getClass().getResourceAsStream(name))
                .transferTo(Files.newOutputStream(fileSystem.getPath(parametersFile)));
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void checkParameters() throws IOException {
        String parametersFile = USER_HOME + "parametersFile";
        String networkParametersFile = USER_HOME + "networkParametersFile";
        String solverParametersFile = USER_HOME + "solverParametersFile";
        String networkParametersId = "networkParametersId";
        SolverType solverType = SolverType.IDA;
        String solverParametersId = "solverParametersId";

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawaltz-default-parameters");
        moduleConfig.setStringProperty("parametersFile", parametersFile);
        moduleConfig.setStringProperty("network.parametersFile", networkParametersFile);
        moduleConfig.setStringProperty("solver.parametersFile", solverParametersFile);
        boolean mergeLoads = true;
        moduleConfig.setStringProperty("mergeLoads", String.valueOf(mergeLoads));
        moduleConfig.setStringProperty("network.parametersId", networkParametersId);
        moduleConfig.setStringProperty("solver.type", solverType.toString());
        moduleConfig.setStringProperty("solver.parametersId", solverParametersId);

        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", parametersFile);
        copyFile("/parametersSet/network.par", networkParametersFile);
        copyFile("/parametersSet/solvers.par", solverParametersFile);

        DynaWaltzParameters parameters = DynaWaltzParameters.load(platformConfig, fileSystem);

        ParametersSet modelsParameters = parameters.getModelsParameters();
        ParametersSet.Parameter booleanParameter = modelsParameters.getParameterSet("test").getParameter("boolean");
        assertEquals("true", booleanParameter.getValue());
        assertEquals("boolean", booleanParameter.getName());
        assertEquals(ParametersSet.ParameterType.BOOL, booleanParameter.getType());
        ParametersSet.Parameter stringParameter = modelsParameters.getParameter("test", "string");
        assertEquals("aString", stringParameter.getValue());
        assertEquals("string", stringParameter.getName());
        assertEquals(ParametersSet.ParameterType.STRING, stringParameter.getType());

        ParametersSet networkParameters = parameters.getNetwork().getParameters();
        assertEquals(networkParametersId, parameters.getNetwork().getParametersId());
        ParametersSet.Parameter loadTp = networkParameters.getParameter(networkParametersId, "load_Tp");
        assertEquals("90", loadTp.getValue());
        assertEquals("load_Tp", loadTp.getName());
        assertEquals(ParametersSet.ParameterType.DOUBLE, loadTp.getType());
        ParametersSet.Parameter loadControllable = networkParameters.getParameter(networkParametersId, "load_isControllable");
        assertEquals("false", loadControllable.getValue());
        assertEquals("load_isControllable", loadControllable.getName());
        assertEquals(ParametersSet.ParameterType.BOOL, loadControllable.getType());

        DynaWaltzParameters.Solver solver = parameters.getSolver();
        ParametersSet solverParameters = solver.getParameters();
        assertEquals(solverParametersId, solver.getParametersId());
        assertEquals(solverType, solver.getType());
        ParametersSet.Parameter order = solverParameters.getParameter(solverParametersId, "order");
        assertEquals("1", order.getValue());
        assertEquals("order", order.getName());
        assertEquals(ParametersSet.ParameterType.INT, order.getType());
        ParametersSet.Parameter absAccuracy = solverParameters.getParameter(solverParametersId, "absAccuracy");
        assertEquals("1e-4", absAccuracy.getValue());
        assertEquals("absAccuracy", absAccuracy.getName());
        assertEquals(ParametersSet.ParameterType.DOUBLE, absAccuracy.getType());

        assertEquals(mergeLoads, parameters.isMergeLoads());
    }

    @Test
    void checkDefaultParameters() throws IOException {
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DynaWaltzParameters.DEFAULT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynaWaltzParameters.DEFAULT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solvers.par", DynaWaltzParameters.DEFAULT_SOLVER_PARAMETERS_FILE);

        DynaWaltzParameters parameters = DynaWaltzParameters.load(platformConfig, fileSystem);
//        assertEquals(parametersFile, parameters.getParametersFile()); FIXME
//        assertEquals(networkParametersFile, parameters.getNetwork().getParametersFile()); FIXME
        assertEquals(DynaWaltzParameters.DEFAULT_NETWORK_PAR_ID, parameters.getNetwork().getParametersId());
        assertEquals(DynaWaltzParameters.DEFAULT_SOLVER_TYPE, parameters.getSolver().getType());
//        assertEquals(solverParametersFile, parameters.getSolver().getParametersFile()); FIXME
        assertEquals(DynaWaltzParameters.DEFAULT_SOLVER_PAR_ID, parameters.getSolver().getParametersId());
        assertEquals(DynaWaltzParameters.DEFAULT_MERGE_LOADS, parameters.isMergeLoads());
    }
}
