/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.Properties;

import org.junit.Test;

import com.google.common.jimfs.Jimfs;
import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.cgmes.model.test.TestGridModel;
import com.powsybl.cgmes.model.test.cim14.Cim14SmallCasesCatalog;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.datasource.ReadOnlyDataSource;
import com.powsybl.dynawo.simulator.DynawoSimulator;
import com.powsybl.iidm.network.Network;
import com.powsybl.triplestore.api.TripleStoreFactory;

public class DynawoSimulatorTest {

    @Test
    public void test() throws Exception {

        Network network = convert(catalog.ieee14());
        DynawoSimulator simulator = new DynawoSimulator(network);
        // FIXME(mathbagu): this test load the configuration from the developer configuration file.
        // simulator.simulate();
    }

    private Network convert(TestGridModel gm) throws IOException {
        String impl = TripleStoreFactory.defaultImplementation();
        try (FileSystem fs = Jimfs.newFileSystem()) {
            PlatformConfig platformConfig = new InMemoryPlatformConfig(fs);
            CgmesImport i = new CgmesImport(platformConfig);
            Properties params = new Properties();
            params.put("storeCgmesModelAsNetworkExtension", "true");
            params.put("powsyblTripleStore", impl);
            ReadOnlyDataSource ds = gm.dataSource();
            Network n = i.importData(ds, params);
            return n;
        }
    }

    private final Cim14SmallCasesCatalog catalog = new Cim14SmallCasesCatalog();
}
