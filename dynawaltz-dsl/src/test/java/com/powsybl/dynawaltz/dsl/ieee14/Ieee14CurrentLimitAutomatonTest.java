/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.ieee14;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynawaltz.dsl.ieee.AbstractIeeeTest;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class Ieee14CurrentLimitAutomatonTest extends AbstractIeeeTest {

    @Before
    public void setup() throws IOException {
        super.setup(
                "/ieee14-currentlimitautomaton/config/models.par",
                "/ieee14-currentlimitautomaton/config/network.par",
                "/ieee14-currentlimitautomaton/config/solvers.par",
                "/ieee14-currentlimitautomaton/powsybl-inputs/IEEE14.iidm",
                "/ieee14-currentlimitautomaton/powsybl-inputs/dynamicModels.groovy",
                "/ieee14-currentlimitautomaton/powsybl-inputs/eventModels.groovy",
                "/ieee14-currentlimitautomaton/powsybl-inputs/curves.groovy",
                "/ieee14-currentlimitautomaton/powsybl-inputs/dynaWaltzParameters.json"
        );
    }

    @Test
    public void testSimulation() throws Exception {
        Ieee14CurrentLimitAutomatonLocalCommandExecutor commandExecutor = new Ieee14CurrentLimitAutomatonLocalCommandExecutor(fileSystem, network, getDynaWaltzSimulationParameters(parameters));
        DynamicSimulationResult result = runSimulation(commandExecutor);
        assertNotNull(result);
    }

    @Override
    public String getWorkingDirName() {
        return "ieee14-currentlimitautomaton";
    }
}
