/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.loadflow.LoadFlowParameters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.powsybl.commons.test.ComparisonUtils.assertTxtEquals;
import static com.powsybl.dynaflow.DynaFlowSecurityAnalysisParameters.MODULE_SPECIFIC_PARAMETERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynaFlowSecurityAnalysisParametersTest extends AbstractSerDeTest {

    private InMemoryPlatformConfig platformConfig;

    @BeforeEach
    @Override
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
    }

    @AfterEach
    @Override
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void checkParameters() {
        double contingenciesStartTime = 24.;
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig(MODULE_SPECIFIC_PARAMETERS);
        moduleConfig.setStringProperty("contingenciesStartTime", Double.toString(contingenciesStartTime));
        DynaFlowSecurityAnalysisParameters saParam = DynaFlowSecurityAnalysisParameters.load(moduleConfig);
        assertEquals(contingenciesStartTime, saParam.getContingenciesStartTime());
    }

    @Test
    void checkAllParametersAssignedToString() {
        DynaFlowSecurityAnalysisParameters saParam = new DynaFlowSecurityAnalysisParameters();
        saParam.update(Map.of("contingenciesStartTime", Double.toString(23d)));
        assertEquals("{contingenciesStartTime=23.0}", saParam.toString());
    }

    @Test
    void defaultParametersSerialization() throws IOException {
        Path workingDir = fileSystem.getPath("dynaflow_sa/workingDir");
        Path parameterFile = fileSystem.getPath(DynaFlowConstants.CONFIG_FILENAME);
        DynaFlowConfigSerializer.serialize(LoadFlowParameters.load(platformConfig),
                new DynaFlowParameters(),
                new DynaFlowSecurityAnalysisParameters(),
                workingDir,
                parameterFile);

        try (InputStream actual = Files.newInputStream(parameterFile);
             InputStream expected = getClass().getResourceAsStream("/params_default_sa.json")) {
            assertNotNull(expected);
            assertTxtEquals(expected, actual);
        }
    }

    @Test
    void parametersSerialization() throws IOException {
        DynaFlowSecurityAnalysisParameters saParam = new DynaFlowSecurityAnalysisParameters()
                .setContingenciesStartTime(20d);
        Path workingDir = fileSystem.getPath("dynaflow_sa/workingDir");
        Path parameterFile = fileSystem.getPath(DynaFlowConstants.CONFIG_FILENAME);
        DynaFlowConfigSerializer.serialize(LoadFlowParameters.load(platformConfig),
                new DynaFlowParameters(),
                saParam,
                workingDir,
                parameterFile);

        try (InputStream actual = Files.newInputStream(parameterFile);
             InputStream expected = getClass().getResourceAsStream("/params_sa.json")) {
            assertNotNull(expected);
            assertTxtEquals(expected, actual);
        }
    }
}
