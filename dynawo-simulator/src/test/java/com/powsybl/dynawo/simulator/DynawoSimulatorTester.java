/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.mockito.Mockito;

import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.cgmes.model.test.TestGridModel;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.datasource.ReadOnlyDataSource;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.dynawo.simulator.results.DynawoResults;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.triplestore.api.TripleStoreFactory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulatorTester {

    public DynawoSimulatorTester(PlatformConfig platformConfig) {
        this.platformConfig = platformConfig;
    }

    public DynawoResults testGridModel(Network network, DynawoProvider provider) throws Exception {
        DynawoSimulator simulator = mockResults(new DynawoSimulator(network, platformConfig));
        simulator.simulate(provider);

        return (DynawoResults) simulator.getResult();
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

    private DynawoSimulator mockResults(DynawoSimulator simulator) {
        DynawoSimulator spySimulator = Mockito.spy(simulator);
        Map<String, String> metrics = new HashMap<>();
        metrics.put("success", "true");
        DynawoResults result = new DynawoResults(metrics);
        result.parseCsv(new File(getClass().getClassLoader().getResource("nordic32/curves.csv").getFile()).toPath());
        Mockito.when(spySimulator.getResult()).thenReturn(result);
        return spySimulator;
    }

    private final PlatformConfig platformConfig;
}
