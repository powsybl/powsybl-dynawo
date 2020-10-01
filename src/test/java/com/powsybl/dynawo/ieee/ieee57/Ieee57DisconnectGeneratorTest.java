/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.ieee.ieee57;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynawo.ieee.AbstractIeeeTest;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Ieee57DisconnectGeneratorTest extends AbstractIeeeTest {

    @Before
    public void setup() throws IOException {
        super.setup(
            "/ieee57-disconnectgenerator/ieee57-disconnectgenerator.par",
            "/ieee57-disconnectgenerator/ieee57-disconnectgenerator-network.par",
            "/ieee57-disconnectgenerator/ieee-solvers.par",
            "/ieee57-disconnectgenerator/IEEE57.iidm",
            "/ieee57-disconnectgenerator/dynamicModels.groovy",
            null,
            "/ieee57-disconnectgenerator/curves.groovy",
            "/ieee57-disconnectgenerator/dynawoParameters.json");
    }

    @Test
    public void testSimulation() throws Exception {
        Ieee57DisconnectGeneratorLocalCommandExecutor commandExecutor = new Ieee57DisconnectGeneratorLocalCommandExecutor(fileSystem, network, getDynawoSimulationParameters(parameters));
        DynamicSimulationResult result = runSimulation(commandExecutor);
        assertNotNull(result);
    }
}
