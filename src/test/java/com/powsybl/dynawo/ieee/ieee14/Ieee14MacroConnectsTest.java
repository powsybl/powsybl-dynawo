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
public class Ieee14MacroConnectsTest extends AbstractIeeeTest {

    @Before
    public void setup() throws IOException {
        super.setup(
                "/ieee14-macroconnects/config/models.par",
                "/ieee14-macroconnects/config/network.par",
                "/ieee14-macroconnects/config/solvers.par",
                "/ieee14-macroconnects/powsybl-inputs/IEEE14.iidm",
                "/ieee14-macroconnects/powsybl-inputs/dynamicModels.groovy",
                null,
                "/ieee14-macroconnects/powsybl-inputs/curves.groovy",
                "/ieee14-macroconnects/powsybl-inputs/dynawoParameters.json"
        );
    }

    @Test
    public void testSimulation() throws Exception {
        Ieee14MacroconnectsLocalCommandExecutor commandExecutor = new Ieee14MacroconnectsLocalCommandExecutor(fileSystem, network, getDynawoSimulationParameters(parameters));
        DynamicSimulationResult result = runSimulation(commandExecutor);
        assertNotNull(result);
    }

    @Override
    public String getWorkingDirName() {
        return "ieee14-macroconnects";
    }
}
