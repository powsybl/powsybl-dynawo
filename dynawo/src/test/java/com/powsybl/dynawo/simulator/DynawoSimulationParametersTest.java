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
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.Solvers;

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

    private void checkValues(DynawoSimulationParameters parameters, Solvers solver, int order) {
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
        Solvers solver = Solvers.IDA;
        int order = 2;

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawo-simulation-default-parameters");
        moduleConfig.setStringProperty("solver", solver.toString());
        moduleConfig.setStringProperty("IDAorder", Integer.toString(order));
        DynawoSimulationParameters parameters = new DynawoSimulationParameters();
        DynawoSimulationParameters.load(parameters, platformConfig);
        checkValues(parameters, solver, order);
    }
}
