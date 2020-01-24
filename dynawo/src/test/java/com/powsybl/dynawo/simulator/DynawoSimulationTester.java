/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import com.google.common.collect.ImmutableList;
import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.cgmes.model.test.TestGridModel;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.datasource.ReadOnlyDataSource;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynamicsimulation.DynamicSimulation;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.simulator.results.DynawoResults;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.triplestore.api.TripleStoreFactory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulationTester {

    public DynawoSimulationTester(boolean mockResults) {
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

    public DynawoResults simulate(Network network, DynawoSimulationParameters dynawoSimulationParameters, PlatformConfig platformConfig)
        throws Exception {
        DynawoSimulationProvider dynawoSimulationProvider = new DynawoSimulationProvider(DynawoConfig.load(platformConfig));
        assertEquals("DynawoSimulation", dynawoSimulationProvider.getName());
        assertEquals("1.0.0", dynawoSimulationProvider.getVersion());
        DynamicSimulation.Runner runner = DynamicSimulation.find(null, ImmutableList.of(dynawoSimulationProvider),
            PlatformConfig.defaultConfig());

        ComputationManager computationManager = new LocalComputationManager(
            LocalComputationConfig.load(platformConfig));
        DynamicSimulationParameters simulationParameters = DynamicSimulationParameters.load(platformConfig);
        simulationParameters.addExtension(DynawoSimulationParameters.class, dynawoSimulationParameters);
        DynawoResults result = (DynawoResults) runner.run(network, computationManager, simulationParameters);
        if (mockResults) {
            result = new DynawoResults(true, null);
            result.parseCsv(getClass().getResourceAsStream("/nordic32/curves.csv"));
        }
        return result;
    }

    private final boolean mockResults;
}
