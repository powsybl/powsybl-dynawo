/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynamicsimulation.CurvesSupplier;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicSimulation;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoProviderTest {

    public static class DynamicModelsSupplierMock implements DynamicModelsSupplier {

        static DynamicModelsSupplier empty() {
            return network -> Collections.emptyList();
        }

        @Override
        public List<DynamicModel> get(Network network) {
            return Collections.emptyList();
        }

    }

    public static class EvenModelsSupplierMock implements EventModelsSupplier {

        static EventModelsSupplier empty() {
            return network -> Collections.emptyList();
        }

        @Override
        public List<EventModel> get(Network network) {
            return Collections.emptyList();
        }

    }

    @Test
    public void test() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Network network = Network.create("test", "test");

            Path localDir = fs.getPath("/tmp");
            ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(localDir, 1));
            DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
            assertEquals(DynawoProvider.NAME, dynawoSimulation.getName());
            assertEquals("1.2.0", dynawoSimulation.getVersion());
            DynamicSimulationResult result = dynawoSimulation.run(network, DynamicModelsSupplierMock.empty(), EvenModelsSupplierMock.empty(),
                                                                  CurvesSupplier.empty(), network.getVariantManager().getWorkingVariantId(),
                                                                  computationManager, DynamicSimulationParameters.load());
            assertNotNull(result);
        }
    }
}
