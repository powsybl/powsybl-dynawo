/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Substation;
import com.powsybl.iidm.network.TopologyKind;
import com.powsybl.iidm.network.VoltageLevel;
import org.junit.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import static org.junit.Assert.*;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzProviderTest {

    private static final String OUTPUT_IIDM_FILENAME = "outputIIDM.xml";

    public static class CurvesSupplierMock implements CurvesSupplier {

        @Override
        public List<Curve> get(Network network) {
            return Collections.singletonList(new DynaWaltzCurve("bus", "uPu"));
        }
    }

    private static class LocalCommandExecutorMock extends AbstractLocalCommandExecutor {

        private final String outputIidm;

        public LocalCommandExecutorMock() {
            this(null);
        }

        public LocalCommandExecutorMock(String outputIidm) {
            this.outputIidm = outputIidm;
        }

        @Override
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
            try {
                Files.createDirectories(workingDir.resolve("outputs").resolve("finalState"));
                if (outputIidm != null) {
                    copyFile(outputIidm, workingDir.resolve("outputs").resolve("finalState").resolve(OUTPUT_IIDM_FILENAME));
                }
                return 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Test
    public void testWithMergeLoads() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Network network = createTestNetwork();

            Path localDir = fs.getPath("/tmp");
            LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/mergedLoads.xiidm");
            ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(localDir, 1), commandExecutor, ForkJoinPool.commonPool());
            DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
            assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
            assertEquals(DynaWaltzProvider.VERSION, dynawoSimulation.getVersion());
            DynamicSimulationResult result = dynawoSimulation.run(network, n -> Collections.emptyList(), EventModelsSupplier.empty(),
                    CurvesSupplier.empty(), network.getVariantManager().getWorkingVariantId(),
                    computationManager, DynamicSimulationParameters.load());
            assertNotNull(result);
        }
    }

    @Test
    public void testWithoutMergeLoads() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Network network = createTestNetwork();

            Path localDir = fs.getPath("/tmp");
            LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/noMergedLoads.xiidm");
            ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(localDir, 1), commandExecutor, ForkJoinPool.commonPool());
            DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
            DynamicSimulationParameters dynamicSimulationParameters = DynamicSimulationParameters.load();
            DynaWaltzParameters dynaWaltzParameters = DynaWaltzParameters.load();
            dynaWaltzParameters.setMergeLoads(false);
            dynamicSimulationParameters.addExtension(DynaWaltzParameters.class, dynaWaltzParameters);

            assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
            DynamicSimulationResult result = dynawoSimulation.run(network, n -> Collections.emptyList(), EventModelsSupplier.empty(),
                    CurvesSupplier.empty(), network.getVariantManager().getWorkingVariantId(),
                    computationManager, dynamicSimulationParameters);
            assertNotNull(result);
        }
    }

    @Test
    public void testFail() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Network network = createTestNetwork();

            Path localDir = fs.getPath("/tmp");
            LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock();
            ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(localDir, 1), commandExecutor, ForkJoinPool.commonPool());
            DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
            assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
            assertEquals(DynaWaltzProvider.VERSION, dynawoSimulation.getVersion());
            DynamicSimulationResult result = dynawoSimulation.run(network, n -> Collections.emptyList(), EventModelsSupplier.empty(),
                    CurvesSupplier.empty(), network.getVariantManager().getWorkingVariantId(),
                    computationManager, DynamicSimulationParameters.load());
            assertNotNull(result);
            assertFalse(result.isOk());
        }
    }

    @Test
    public void testWithoutCurves() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Network network = createTestNetwork();

            Path localDir = fs.getPath("/tmp");
            LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/mergedLoads.xiidm");
            ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(localDir, 1), commandExecutor, ForkJoinPool.commonPool());
            DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
            assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
            assertEquals(DynaWaltzProvider.VERSION, dynawoSimulation.getVersion());
            DynamicSimulationResult result = dynawoSimulation.run(network, n -> Collections.emptyList(), EventModelsSupplier.empty(),
                    new CurvesSupplierMock(), network.getVariantManager().getWorkingVariantId(),
                    computationManager, DynamicSimulationParameters.load());
            assertNotNull(result);
            assertFalse(result.isOk());
        }
    }

    private static Network createTestNetwork() {
        Network network = Network.create("test", "test");
        Substation s = network.newSubstation().setId("substation").add();
        VoltageLevel vl = s.newVoltageLevel().setId("vl1").setNominalV(400).setTopologyKind(TopologyKind.NODE_BREAKER).add();
        vl.getNodeBreakerView().newBusbarSection().setId("Busbar").setNode(0).add();
        vl.getNodeBreakerView().newDisconnector().setNode1(0).setNode2(1).setId("breaker1").add();
        vl.getNodeBreakerView().newDisconnector().setNode1(0).setNode2(2).setId("breaker2").add();
        vl.newLoad().setId("load1").setP0(10.0).setQ0(5.0).setNode(1).add();
        vl.newLoad().setId("load2").setP0(12.0).setQ0(1.0).setNode(2).add();
        return network;
    }
}
