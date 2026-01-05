/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.json;

import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static com.powsybl.dynawo.DynawoSimulationParameters.MODULE_SPECIFIC_PARAMETERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationParametersJsonTest extends AbstractSerDeTest {

    private static final String USER_HOME = "/home/user/";

    private InMemoryPlatformConfig platformConfig;

    @BeforeEach
    @Override
    public void setUp() throws IOException {
        super.setUp();
        platformConfig = new InMemoryPlatformConfig(fileSystem);
        Files.createDirectories(fileSystem.getPath(USER_HOME));
        copyFile("/parametersSet/models.par", DynawoSimulationParameters.DEFAULT_INPUT_PARAMETERS_FILE);
        copyFile("/parametersSet/network.par", DynawoSimulationParameters.DEFAULT_INPUT_NETWORK_PARAMETERS_FILE);
        copyFile("/parametersSet/solvers.par", DynawoSimulationParameters.DEFAULT_INPUT_SOLVER_PARAMETERS_FILE);
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig(MODULE_SPECIFIC_PARAMETERS);
        moduleConfig.setStringProperty("parametersFile", "/work/inmemory/models.par");
        moduleConfig.setStringProperty("network.parametersFile", "/work/inmemory/network.par");
        moduleConfig.setStringProperty("solver.parametersFile", "/work/inmemory/solvers.par");
    }

    private void copyFile(String name, String parametersFile) throws IOException {
        Path path = platformConfig.getConfigDir()
                .map(cd -> cd.resolve(fileSystem.getPath(parametersFile)))
                .orElse(fileSystem.getPath(parametersFile));
        Objects.requireNonNull(getClass().getResourceAsStream(name))
                .transferTo(Files.newOutputStream(path));
    }

    @Test
    void roundTrip() throws IOException {
        CriticalTimeCalculationParameters parameters = CriticalTimeCalculationParameters.builder()
                .setDynawoParameters(DynawoSimulationParameters.load(platformConfig))
                .setDebugDir("/tmp/debugDir")
                .build();
        roundTripTest(parameters, JsonCriticalTimeCalculationParameters::write, JsonCriticalTimeCalculationParameters::read, "/CriticalTimeCalculationParameters.json");
    }

    @Test
    void roundTripNoDebugDir() throws IOException {
        CriticalTimeCalculationParameters parameters = CriticalTimeCalculationParameters.builder()
                .setDynawoParameters(DynawoSimulationParameters.load(platformConfig))
                .build();
        roundTripTest(parameters, JsonCriticalTimeCalculationParameters::write, JsonCriticalTimeCalculationParameters::read, "/CriticalTimeCalculationParametersNoDebugDir.json");
    }

    @Test
    void readError() throws IOException {
        try (var is = getClass().getResourceAsStream("/CriticalTimeCalculationParametersError.json")) {
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> JsonCriticalTimeCalculationParameters.read(is));
            assertEquals("Unexpected field: unknownParameter", e.getMessage());
        }
    }

}
