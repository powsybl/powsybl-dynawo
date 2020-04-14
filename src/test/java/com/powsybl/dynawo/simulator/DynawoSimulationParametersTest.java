/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.file.FileSystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverType;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulationParametersTest {

    private InMemoryPlatformConfig platformConfig;
    private FileSystem fileSystem;

    @Before
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
        platformConfig.createModuleConfig("dynawo-default-parameters");
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void checkParameters() {
        String parametersFile = "/home/user/parametersDDB";
        String networkParametersId = "networkParametersId";
        SolverType solverType = SolverType.IDA;
        String solverParametersFile = "/home/user/parameters";
        String solverParametersId = "solverParametersId";

        MapModuleConfig moduleConfig = (MapModuleConfig) platformConfig.getModuleConfig("dynawo-default-parameters");
        moduleConfig.setStringProperty("parametersFile", parametersFile);
        moduleConfig.setStringProperty("network.ParametersId", networkParametersId);
        moduleConfig.setStringProperty("solver.type", solverType.toString());
        moduleConfig.setStringProperty("solver.parametersFile", solverParametersFile);
        moduleConfig.setStringProperty("solver.parametersId", solverParametersId);

        DynawoSimulationParameters parameters = DynawoSimulationParameters.load(platformConfig);
        assertEquals(parametersFile, parameters.getParametersFile());
        assertEquals(networkParametersId, parameters.getNetworkParametersId());
        assertEquals(solverType, parameters.getSolverType());
        assertEquals(solverParametersFile, parameters.getSolverParametersFile());
        assertEquals(solverParametersId, parameters.getSolverParametersId());
    }

    @Test
    public void checkDefaultParameters() {

        DynawoSimulationParameters parameters = DynawoSimulationParameters.load(platformConfig);
        assertNull(parameters.getParametersFile());
        assertEquals("NETWORK", parameters.getNetworkParametersId());
        assertEquals(DynawoSimulationParameters.DEFAULT_SOLVER_TYPE, parameters.getSolverType());
        assertNull(parameters.getSolverParametersFile());
        assertEquals("SIM", parameters.getSolverParametersId());
    }
}
