/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.cgmes.model.test.cim14.Cim14SmallCasesCatalog;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.simulation.ImpactAnalysisResult;

public class DynawoSimulatorTest {

    @Test
    public void test() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {

            PlatformConfig platformConfig = configure(fs);
            DynawoSimulatorTester tester = new DynawoSimulatorTester(platformConfig);
            ImpactAnalysisResult result = tester.testGridModel(catalog.nordic32());
            LOGGER.info("metrics " + result.getMetrics().get("success"));
            assertTrue(!Boolean.parseBoolean(result.getMetrics().get("success")));
        }
    }

    private PlatformConfig configure(FileSystem fs) throws IOException {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fs);
        Files.createDirectory(fs.getPath("/dynawoPath"));
        Files.createDirectory(fs.getPath("/workingPath"));
        Files.createDirectories(fs.getPath("/tmp"));
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("import-export-parameters-default-value");
        moduleConfig.setStringProperty("iidm.export.xml.extensions", "null");
        moduleConfig = platformConfig.createModuleConfig("computation-local");
        moduleConfig.setStringProperty("tmpDir", "/tmp");
        moduleConfig = platformConfig.createModuleConfig("dynawo");
        moduleConfig.setStringProperty("dynawoHomeDir", "/dynawoPath");
        moduleConfig.setStringProperty("workingDir", "/workingPath");
        moduleConfig.setStringProperty("debug", "false");
        moduleConfig.setStringProperty("dynawoCptCommandName", "myEnvDynawo.sh");
        moduleConfig = platformConfig.createModuleConfig("simulation-parameters");
        moduleConfig.setStringProperty("preFaultSimulationStopInstant", "1");
        moduleConfig.setStringProperty("postFaultSimulationStopInstant", "60");
        moduleConfig.setStringProperty("faultEventInstant", "30");
        moduleConfig.setStringProperty("branchSideOneFaultShortCircuitDuration", "60");
        moduleConfig.setStringProperty("branchSideTwoFaultShortCircuitDuration", "60");
        moduleConfig.setStringProperty("generatorFaultShortCircuitDuration", "60");
        return platformConfig;
    }

    private final Cim14SmallCasesCatalog catalog = new Cim14SmallCasesCatalog();
    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoSimulatorTest.class);
}
