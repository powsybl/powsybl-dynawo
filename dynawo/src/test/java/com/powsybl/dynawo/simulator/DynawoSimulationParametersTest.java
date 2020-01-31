/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
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
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverIDAParameters;
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
    public void testNoConfig() {
        DynawoSimulationParameters parameters = new DynawoSimulationParameters();
        DynawoSimulationParameters.load(parameters, platformConfig);
        assertEquals(DynawoSimulationParameters.DEFAULT_SOLVER_TYPE, parameters.getSolverParameters().getType());
    }

    @Test
    public void checkConfig() {
        SolverType solverType = SolverType.IDA;
        int idaOrder = 2;

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawo-simulation-default-parameters");
        moduleConfig.setStringProperty("solver", solverType.toString());
        moduleConfig.setStringProperty("IDAorder", Integer.toString(idaOrder));
        DynawoSimulationParameters parameters = new DynawoSimulationParameters();
        DynawoSimulationParameters.load(parameters, platformConfig);
        assertEquals(SolverType.IDA, parameters.getSolverParameters().getType());
        assertEquals(idaOrder, ((SolverIDAParameters) parameters.getSolverParameters()).getOrder());
    }
}
