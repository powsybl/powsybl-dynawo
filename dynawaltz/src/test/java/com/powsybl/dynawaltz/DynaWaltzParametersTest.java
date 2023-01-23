/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.Optional;

import com.powsybl.commons.config.ModuleConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.dynawaltz.DynaWaltzParameters.SolverType;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzParametersTest {

    private final String parametersFile = "/home/user/parametersFile";
    private final String networkParametersFile = "/home/user/networkParametersFile";
    private final String solverParametersFile = "/home/user/solverParametersFile";

    private InMemoryPlatformConfig platformConfig;
    private FileSystem fileSystem;

    @Before
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawaltz-default-parameters");
        moduleConfig.setStringProperty("parametersFile", parametersFile);
        moduleConfig.setStringProperty("network.parametersFile", networkParametersFile);
        moduleConfig.setStringProperty("solver.parametersFile", solverParametersFile);
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void checkParameters() {
        String networkParametersId = "networkParametersId";
        SolverType solverType = SolverType.IDA;
        String solverParametersId = "solverParametersId";

        Optional<ModuleConfig> moduleConfig = platformConfig.getOptionalModuleConfig("dynawaltz-default-parameters");
        moduleConfig.filter(MapModuleConfig.class::isInstance).map(MapModuleConfig.class::cast).ifPresent(c -> {
            c.setStringProperty("network.parametersId", networkParametersId);
            c.setStringProperty("solver.type", solverType.toString());
            c.setStringProperty("solver.parametersId", solverParametersId);
        });

        DynaWaltzParameters parameters = DynaWaltzParameters.load(platformConfig);
        assertEquals(parametersFile, parameters.getParametersFile());
        assertEquals(networkParametersFile, parameters.getNetwork().getParametersFile());
        assertEquals(networkParametersId, parameters.getNetwork().getParametersId());
        assertEquals(solverType, parameters.getSolver().getType());
        assertEquals(solverParametersFile, parameters.getSolver().getParametersFile());
        assertEquals(solverParametersId, parameters.getSolver().getParametersId());
    }

    @Test
    public void checkDefaultParameters() {

        DynaWaltzParameters parameters = DynaWaltzParameters.load(platformConfig);
        assertEquals(parametersFile, parameters.getParametersFile());
        assertEquals(networkParametersFile, parameters.getNetwork().getParametersFile());
        assertEquals(DynaWaltzParameters.DEFAULT_NETWORK_PAR_ID, parameters.getNetwork().getParametersId());
        assertEquals(DynaWaltzParameters.DEFAULT_SOLVER_TYPE, parameters.getSolver().getType());
        assertEquals(solverParametersFile, parameters.getSolver().getParametersFile());
        assertEquals(DynaWaltzParameters.DEFAULT_SOLVER_PAR_ID, parameters.getSolver().getParametersId());
    }
}
