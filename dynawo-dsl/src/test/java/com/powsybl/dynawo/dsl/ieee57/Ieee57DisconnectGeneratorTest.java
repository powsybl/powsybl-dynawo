/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl.ieee57;

import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynawo.dsl.ieee.AbstractIeeeTest;
import com.powsybl.dynawo.dsl.ieee.DynawoLocalCommandExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class Ieee57DisconnectGeneratorTest extends AbstractIeeeTest {

    @BeforeEach
    void setup() throws IOException {
        super.setup(
            "/ieee57-disconnectgenerator/config/models.par",
            "/ieee57-disconnectgenerator/config/network.par", "51",
            "/ieee57-disconnectgenerator/config/solvers.par", "2",
            "/ieee57-disconnectgenerator/powsybl-inputs/IEEE57.iidm",
            "/ieee57-disconnectgenerator/powsybl-inputs/dynamicModels.groovy",
            "/ieee57-disconnectgenerator/powsybl-inputs/eventModels.groovy",
            "/ieee57-disconnectgenerator/powsybl-inputs/curves.groovy",
            0, 30);
    }

    @Test
    void testSimulation() throws Exception {
        DynawoLocalCommandExecutor commandExecutor = new DynawoLocalCommandExecutor(fileSystem, network.getId(), getDynamicSimulationParameters(parameters), getWorkingDirName(), "/dynawo_version.out");
        DynamicSimulationResult result = runSimulation(commandExecutor);
        assertNotNull(result);
    }

    @Override
    public String getWorkingDirName() {
        return "ieee57-disconnectgenerator";
    }
}
