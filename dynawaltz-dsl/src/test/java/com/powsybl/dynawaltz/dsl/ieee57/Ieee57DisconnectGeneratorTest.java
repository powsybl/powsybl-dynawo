/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.ieee57;

import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynawaltz.dsl.ieee.AbstractIeeeTest;
import com.powsybl.dynawaltz.dsl.ieee.DynaWaltzLocalCommandExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class Ieee57DisconnectGeneratorTest extends AbstractIeeeTest {

    @BeforeEach
    void setup() throws IOException {
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
    void testSimulation() throws Exception {
        DynaWaltzLocalCommandExecutor commandExecutor = new DynaWaltzLocalCommandExecutor(fileSystem, network.getId(), getDynaWaltzSimulationParameters(parameters), getWorkingDirName(), "/dynawo_version.out");
        DynamicSimulationResult result = runSimulation(commandExecutor);
        assertNotNull(result);
    }

    @Override
    public String getWorkingDirName() {
        return "ieee57-disconnectgenerator";
    }
}
