/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.ieee57;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import com.powsybl.dynawaltz.dsl.ieee.AbstractIeeeTest;
import org.junit.Before;
import org.junit.Test;

import com.powsybl.dynamicsimulation.DynamicSimulationResult;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Ieee57DisconnectGeneratorTest extends AbstractIeeeTest {

    @Before
    public void setup() throws IOException {
        super.setup(
            "/ieee57-disconnectgenerator/config/models.par",
            "/ieee57-disconnectgenerator/config/network.par",
            "/ieee57-disconnectgenerator/config/solvers.par",
            "/ieee57-disconnectgenerator/powsybl-inputs/IEEE57.iidm",
            "/ieee57-disconnectgenerator/powsybl-inputs/dynamicModels.groovy",
            "/ieee57-disconnectgenerator/powsybl-inputs/eventModels.groovy",
            "/ieee57-disconnectgenerator/powsybl-inputs/curves.groovy",
            "/ieee57-disconnectgenerator/powsybl-inputs/dynaWaltzParameters.json");
    }

    @Test
    public void testSimulation() throws Exception {
        Ieee57DisconnectGeneratorLocalCommandExecutor commandExecutor = new Ieee57DisconnectGeneratorLocalCommandExecutor(fileSystem, network, getDynaWaltzSimulationParameters(parameters));
        DynamicSimulationResult result = runSimulation(commandExecutor);
        assertNotNull(result);
    }

    @Override
    public String getWorkingDirName() {
        return "ieee57-disconnectgenerator";
    }
}
