/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.cgmes.model.test.TestGridModel;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.datasource.ReadOnlyDataSource;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynawo.DynawoExporter;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.dynawo.simulator.results.DynawoResults;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.simulation.SimulationParameters;
import com.powsybl.triplestore.api.TripleStoreFactory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulatorTester {

    public DynawoSimulatorTester(PlatformConfig platformConfig, boolean mockResults) {
        this.platformConfig = platformConfig;
        this.mockResults = mockResults;
    }

    public Network convert(PlatformConfig platformConfig, TestGridModel gm) throws IOException {
        String impl = TripleStoreFactory.defaultImplementation();
        CgmesImport i = new CgmesImport(platformConfig);
        Properties params = new Properties();
        params.put("storeCgmesModelAsNetworkExtension", "true");
        params.put("powsyblTripleStore", impl);
        ReadOnlyDataSource ds = gm.dataSource();
        Network n = i.importData(ds, NetworkFactory.findDefault(), params);
        return n;
    }

    public DynawoResults simulate(Network network, DynawoProvider provider, DynawoExporter exporter,
        PlatformConfig platformConfig)
        throws Exception {
        ComputationManager computationManager = new LocalComputationManager(
            LocalComputationConfig.load(platformConfig));
        SimulationParameters simulationParameters = SimulationParameters.load(platformConfig);
        DynawoConfig dynawoConfig = DynawoConfig.load(platformConfig);
        DynawoSimulator simulator = new DynawoSimulator(simulationParameters, computationManager, exporter,
            dynawoConfig);
        DynawoResults result = (DynawoResults) simulator.simulate(network, provider);
        if (mockResults) {
            Map<String, String> metrics = new HashMap<>();
            metrics.put("success", "true");
            result = new DynawoResults(metrics);
            result.parseCsv(getClass().getResourceAsStream("/nordic32/curves.csv"));
        }
        return result;
    }

    private final boolean mockResults;
    private final PlatformConfig platformConfig;
}
