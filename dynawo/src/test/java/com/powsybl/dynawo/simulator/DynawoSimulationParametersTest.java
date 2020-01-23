/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import static org.junit.Assert.assertEquals;

import java.nio.file.FileSystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.Solver;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulationParametersTest {

    InMemoryPlatformConfig platformConfig;
    FileSystem fileSystem;

    @Before
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
    }

    @After
    public void tearDown() throws Exception {
        fileSystem.close();
    }

    private void checkValues(DynawoSimulationParameters parameters, Solver solver, int order) {
        assertEquals(parameters.getSolver(), solver);
        assertEquals(parameters.getIdaOrder(), order);
    }

    @Test
    public void testNoConfig() {
        DynawoSimulationParameters parameters = new DynawoSimulationParameters();
        DynawoSimulationParameters.load(parameters, platformConfig);
        checkValues(parameters, DynawoSimulationParameters.DEFAULT_SOLVER, DynawoSimulationParameters.DEFAULT_IDA_ORDER);
    }

    @Test
    public void checkConfig() throws Exception {
        Solver solver = Solver.IDA;
        int order = 2;

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawo-simulation-default-parameters");
        moduleConfig.setStringProperty("solver", solver.toString());
        moduleConfig.setStringProperty("IDAorder", Integer.toString(order));
        DynawoSimulationParameters parameters = new DynawoSimulationParameters();
        DynawoSimulationParameters.load(parameters, platformConfig);
        checkValues(parameters, solver, order);
    }
}
