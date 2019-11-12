/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.powsybl.iidm.network.impl.NetworkFactoryImpl;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.cgmes.model.test.TestGridModel;
import com.powsybl.cgmes.model.test.cim14.Cim14SmallCasesCatalog;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.datasource.ReadOnlyDataSource;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynawo.simulator.DynawoConfig;
import com.powsybl.dynawo.simulator.DynawoSimulator;
import com.powsybl.dynawo.simulator.results.DynawoResults;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.simulation.SimulationParameters;
import com.powsybl.triplestore.api.TripleStoreFactory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulatorTest {

    @Test
    public void test() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            PlatformConfig platformConfig = configure(fs);
            Network network = convert(platformConfig, Cim14SmallCasesCatalog.nordic32());
            DynawoResults result = simulate(network, platformConfig);
            assertTrue(Boolean.parseBoolean(result.getMetrics().get("success")));

            //check final voltage of bus close to the event
            int index = result.getNames().indexOf("NETWORK__N1011____TN_Upu_value");
            assertEquals(result.getTimeSeries().get(new Double(30.0)).get(index), new Double(0.931558));
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

    private Network convert(PlatformConfig platformConfig, TestGridModel gm) throws IOException {
        String impl = TripleStoreFactory.defaultImplementation();
        CgmesImport i = new CgmesImport(platformConfig);
        Properties params = new Properties();
        params.put("storeCgmesModelAsNetworkExtension", "true");
        params.put("powsyblTripleStore", impl);
        ReadOnlyDataSource ds = gm.dataSource();
        Network n = i.importData(ds, NetworkFactory.findDefault(), params);
        return n;
    }

    private DynawoResults simulate(Network network, PlatformConfig platformConfig) throws Exception {
        ComputationManager computationManager = new LocalComputationManager(
            LocalComputationConfig.load(platformConfig));
        SimulationParameters simulationParameters = SimulationParameters.load(platformConfig);
        DynawoConfig dynawoConfig = DynawoConfig.load(platformConfig);
        XMLExporter exporter = new XMLExporter(platformConfig);
        DynawoSimulator simulator = new DynawoSimulator(simulationParameters, computationManager, exporter, dynawoConfig);
        simulator.simulate(network);
        Map<String, String> metrics = new HashMap<>();
        metrics.put("success", "true");
        DynawoResults result = new DynawoResults(metrics);
        result.parseCsv(getClass().getResourceAsStream("/nordic32/curves.csv"));
        return result;
    }
}
