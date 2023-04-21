/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.ieee14;

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
class Ieee14DisconnectLineTest extends AbstractIeeeTest {

    @BeforeEach
    public void setup() throws IOException {
        super.setup(
            "/ieee14-disconnectline/config/models.par",
            "/ieee14-disconnectline/config/network.par", "8",
            "/ieee14-disconnectline/config/solvers.par", "2",
            "/ieee14-disconnectline/powsybl-inputs/IEEE14.iidm",
            "/ieee14-disconnectline/powsybl-inputs/dynamicModels.groovy",
            "/ieee14-disconnectline/powsybl-inputs/eventModels.groovy",
            "/ieee14-disconnectline/powsybl-inputs/curves.groovy",
            0, 30);
    }

    @Test
    void testSimulation() throws Exception {
        DynaWaltzLocalCommandExecutor commandExecutor = new DynaWaltzLocalCommandExecutor(fileSystem, network.getId(), getDynaWaltzSimulationParameters(parameters), getWorkingDirName(), "/dynawo_version.out");
        DynamicSimulationResult result;
        result = runSimulation(commandExecutor);
        assertNotNull(result);
    }

    @Override
    public String getWorkingDirName() {
        return "ieee14-disconnectline";
    }
}
