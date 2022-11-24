/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.dynamicsimulation.*;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.iidm.network.Network;

import static org.junit.Assert.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzProviderTest {

    private static final String OUTPUT_IIDM_FILENAME = "outputIIDM.xml";

    public static class DynamicModelsSupplierMock implements DynamicModelsSupplier {

        static DynamicModelsSupplier empty() {
            return network -> Collections.emptyList();
        }

        @Override
        public List<DynamicModel> get(Network network) {
            return Collections.emptyList();
        }

    }

    public static class EventModelsSupplierMock implements EventModelsSupplier {

        static EventModelsSupplier empty() {
            return network -> Collections.emptyList();
        }

        @Override
        public List<EventModel> get(Network network) {
            return Collections.emptyList();
        }

    }

    public static class CurvesSupplierMock implements CurvesSupplier {

        @Override
        public List<Curve> get(Network network) {
            return Collections.singletonList(new DynaWaltzCurve("bus", "uPu"));
        }
    }

    private static class EmptyLocalCommandExecutorMock extends AbstractLocalCommandExecutor {

        @Override
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
            try {
                Files.createDirectories(workingDir.resolve("outputs").resolve("finalState"));
                return 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static class WithoutCurvesLocalCommandExecutorMock extends AbstractLocalCommandExecutor {

        private final String outputIidm;

        public WithoutCurvesLocalCommandExecutorMock(String outputIidm) {
            this.outputIidm = Objects.requireNonNull(outputIidm);
        }

        @Override
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
            try {
                Files.createDirectories(workingDir.resolve("outputs").resolve("finalState"));
                copyFile(outputIidm, workingDir.resolve("outputs").resolve("finalState").resolve(OUTPUT_IIDM_FILENAME));
                return 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Test
    public void testWithMergeLoads() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Network network = Network.create("test", "test");

            Path localDir = fs.getPath("/tmp");
            ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(localDir, 1));
            DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
            assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
            assertEquals("1.2.0", dynawoSimulation.getVersion());
            DynamicSimulationResult result = dynawoSimulation.run(network, DynamicModelsSupplierMock.empty(), EventModelsSupplierMock.empty(),
                                                                  CurvesSupplier.empty(), network.getVariantManager().getWorkingVariantId(),
                                                                  computationManager, DynamicSimulationParameters.load());
            assertNotNull(result);
        }
    }

    @Test
    public void testWithoutMergeLoads() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Network network = Network.create("test", "test");

            Path localDir = fs.getPath("/tmp");
            ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(localDir, 1));
            DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
            DynamicSimulationParameters dynamicSimulationParameters = DynamicSimulationParameters.load();
            DynaWaltzParameters dynaWaltzParameters = DynaWaltzParameters.load();
            dynaWaltzParameters.setMergeLoads(false);
            dynamicSimulationParameters.addExtension(DynaWaltzParameters.class, dynaWaltzParameters);

            assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
            assertEquals("1.2.0", dynawoSimulation.getVersion());
            DynamicSimulationResult result = dynawoSimulation.run(network, DynamicModelsSupplierMock.empty(), EventModelsSupplierMock.empty(),
                    CurvesSupplier.empty(), network.getVariantManager().getWorkingVariantId(),
                    computationManager, dynamicSimulationParameters);
            assertNotNull(result);
        }
    }

    @Test
    public void testFailWithMergeLoads() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Network network = Network.create("test", "test");

            Path localDir = fs.getPath("/tmp");
            LocalCommandExecutor commandExecutor = new EmptyLocalCommandExecutorMock();
            ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(localDir, 1), commandExecutor, ForkJoinPool.commonPool());
            DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
            assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
            assertEquals("1.2.0", dynawoSimulation.getVersion());
            DynamicSimulationResult result = dynawoSimulation.run(network, DynamicModelsSupplierMock.empty(), EventModelsSupplierMock.empty(),
                    CurvesSupplier.empty(), network.getVariantManager().getWorkingVariantId(),
                    computationManager, DynamicSimulationParameters.load());
            assertNotNull(result);
            assertFalse(result.isOk());
        }
    }

    @Test
    public void testFailWithoutMergeLoads() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Network network = Network.create("test", "test");

            Path localDir = fs.getPath("/tmp");
            LocalCommandExecutor commandExecutor = new EmptyLocalCommandExecutorMock();
            ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(localDir, 1), commandExecutor, ForkJoinPool.commonPool());
            DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
            DynamicSimulationParameters dynamicSimulationParameters = DynamicSimulationParameters.load();
            DynaWaltzParameters dynaWaltzParameters = DynaWaltzParameters.load();
            dynaWaltzParameters.setMergeLoads(false);
            dynamicSimulationParameters.addExtension(DynaWaltzParameters.class, dynaWaltzParameters);
            assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
            assertEquals("1.2.0", dynawoSimulation.getVersion());
            DynamicSimulationResult result = dynawoSimulation.run(network, DynamicModelsSupplierMock.empty(), EventModelsSupplierMock.empty(),
                    CurvesSupplier.empty(), network.getVariantManager().getWorkingVariantId(),
                    computationManager, dynamicSimulationParameters);
            assertNotNull(result);
            assertFalse(result.isOk());
        }
    }

    @Test
    public void testWithoutCurvesWithMergeLoads() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Network network = Network.create("test", "test");

            Path localDir = fs.getPath("/tmp");
            LocalCommandExecutor commandExecutor = new WithoutCurvesLocalCommandExecutorMock("/test.xiidm");
            ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(localDir, 1), commandExecutor, ForkJoinPool.commonPool());
            DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
            assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
            assertEquals("1.2.0", dynawoSimulation.getVersion());
            DynamicSimulationResult result = dynawoSimulation.run(network, DynamicModelsSupplierMock.empty(), EventModelsSupplierMock.empty(),
                    new CurvesSupplierMock(), network.getVariantManager().getWorkingVariantId(),
                    computationManager, DynamicSimulationParameters.load());
            assertNotNull(result);
            assertFalse(result.isOk());
        }
    }

    @Test
    public void testWithoutCurvesWithoutMergeLoads() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
            Network network = Network.create("test", "test");

            Path localDir = fs.getPath("/tmp");
            LocalCommandExecutor commandExecutor = new WithoutCurvesLocalCommandExecutorMock("/test.xiidm");
            ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(localDir, 1), commandExecutor, ForkJoinPool.commonPool());
            DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
            DynamicSimulationParameters dynamicSimulationParameters = DynamicSimulationParameters.load();
            DynaWaltzParameters dynaWaltzParameters = DynaWaltzParameters.load();
            dynaWaltzParameters.setMergeLoads(false);
            dynamicSimulationParameters.addExtension(DynaWaltzParameters.class, dynaWaltzParameters);
            assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
            assertEquals("1.2.0", dynawoSimulation.getVersion());
            DynamicSimulationResult result = dynawoSimulation.run(network, DynamicModelsSupplierMock.empty(), EventModelsSupplierMock.empty(),
                    new CurvesSupplierMock(), network.getVariantManager().getWorkingVariantId(),
                    computationManager, dynamicSimulationParameters);
            assertNotNull(result);
            assertFalse(result.isOk());
        }
    }
}
