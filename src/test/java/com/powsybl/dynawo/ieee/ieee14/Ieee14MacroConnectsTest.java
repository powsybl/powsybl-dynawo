/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.ieee.ieee14;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
                "/ieee14-macroconnects/ieee14-macroconnects.par",
                "/ieee14-macroconnects/ieee14-macroconnects-network.par",
                "/ieee14-macroconnects/ieee-solvers.par",
                "/ieee14-macroconnects/IEEE14.iidm",
                "/ieee14-macroconnects/dynamicModels.groovy",
                null,
                "/ieee14-macroconnects/curves.groovy",
                "/ieee14-macroconnects/dynawoParameters.json"
        );
    }

    @Test
    public void testSimulation() throws Exception {
        Ieee14MacroconnectsLocalCommandExecutor commandExecutor = new Ieee14MacroconnectsLocalCommandExecutor(fileSystem, network, getDynawoSimulationParameters(parameters));
        DynamicSimulationResult result = runSimulation(commandExecutor);
        assertNotNull(result);
    }

    @Override
    public Path getWorkingDir() throws IOException {
        return Files.createDirectory(fileSystem.getPath("ieee14-macroconnects"));
    }
}
