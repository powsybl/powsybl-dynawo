/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import static org.junit.Assert.assertEquals;

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
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void checkParameters() {
        String parametersDDB = "/home/user/parametersDDB";
        String networkParametersId = "networkParametersId";
        SolverType solverType = SolverType.IDA;
        String solverParametersFile = "/home/user/parameters";
        String solverParametersId = "solverParametersId";

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawo-default-parameters");
        moduleConfig.setStringProperty("parametersDDB", parametersDDB);
        moduleConfig.setStringProperty("networkParametersId", networkParametersId);

        moduleConfig = platformConfig.createModuleConfig("dynawo-solver-default-parameters");
        moduleConfig.setStringProperty("type", solverType.toString());
        moduleConfig.setStringProperty("parametersFile", solverParametersFile);
        moduleConfig.setStringProperty("parametersId", solverParametersId);

        DynawoSimulationParameters parameters = DynawoSimulationParameters.load(platformConfig);
        assertEquals(parametersDDB, parameters.getParametersDDB());
        assertEquals(networkParametersId, parameters.getNetworkParametersId());
        assertEquals(solverType, parameters.getSolverType());
        assertEquals(solverParametersFile, parameters.getSolverParametersFile());
        assertEquals(solverParametersId, parameters.getSolverParametersId());
    }
}
