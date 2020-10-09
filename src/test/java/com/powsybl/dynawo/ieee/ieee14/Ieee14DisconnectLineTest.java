/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.ieee.ieee14;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynawo.ieee.AbstractIeeeTest;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Ieee14DisconnectLineTest extends AbstractIeeeTest {

    @Before
    public void setup() throws IOException {
        super.setup(
            "/ieee14-disconnectline/ieee14-disconnectline.par",
            "/ieee14-disconnectline/ieee14-disconnectline-network.par",
            "/ieee14-disconnectline/ieee-solvers.par",
            "/ieee14-disconnectline/IEEE14.iidm",
            "/ieee14-disconnectline/dynamicModels.groovy",
            "/ieee14-disconnectline/eventModels.groovy",
            "/ieee14-disconnectline/curves.groovy",
            "/ieee14-disconnectline/dynawoParameters.json");
    }

    @Test
    public void testSimulation() throws Exception {
        Ieee14DisconnectLineLocalCommandExecutor commandExecutor = new Ieee14DisconnectLineLocalCommandExecutor(fileSystem, network, getDynawoSimulationParameters(parameters));
        DynamicSimulationResult result;
        result = runSimulation(commandExecutor);
        assertNotNull(result);
    }

    @Override
    public String getWorkingDirName() {
        return "ieee14-disconnectline";
    }
}
