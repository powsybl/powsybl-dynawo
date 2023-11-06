/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.test.AbstractConverterTest;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynamicsimulation.*;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Substation;
import com.powsybl.iidm.network.TopologyKind;
import com.powsybl.iidm.network.VoltageLevel;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class DynaWaltzProviderTest extends AbstractConverterTest {

    private static final String OUTPUT_IIDM_FILENAME = "outputIIDM.xml";
    private DynaWaltzConfig config;

    @BeforeEach
    public void setUp() throws IOException {
        super.setUp();
        config = DynaWaltzConfig.load();
    }

    public static class CurvesSupplierMock implements CurvesSupplier {

        @Override
        public List<Curve> get(Network network) {
            return Collections.singletonList(new DynaWaltzCurve("bus", "uPu"));
        }
    }

    private static class LocalCommandExecutorMock extends AbstractLocalCommandExecutor {

        private final String stdOutFileRef;
        private final String outputIidm;

        public LocalCommandExecutorMock(String stdOutFileRef, String outputIidm) {
            this.stdOutFileRef = stdOutFileRef;
            this.outputIidm = outputIidm;
        }

        @Override
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
            try {
                copyFile(stdOutFileRef, outFile);
                Files.createDirectories(workingDir.resolve("outputs").resolve("finalState"));
                copyFile(outputIidm, workingDir.resolve("outputs").resolve("finalState").resolve(OUTPUT_IIDM_FILENAME));
                return 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Test
    void testWithMergeLoads() throws Exception {
        Network network = createTestNetwork();
        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynawo_version.out", "/mergedLoads.xiidm");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(tmpDir, 1), commandExecutor, ForkJoinPool.commonPool());
        DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
        assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
        DynamicSimulationResult result = dynawoSimulation.run(network, n -> Collections.emptyList(), EventModelsSupplier.empty(),
                CurvesSupplier.empty(), network.getVariantManager().getWorkingVariantId(),
                computationManager, DynamicSimulationParameters.load());
        assertNotNull(result);
    }

    @Test
    void testWithoutMergeLoads() throws Exception {
        Network network = createTestNetwork();
        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynawo_version.out", "/noMergedLoads.xiidm");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(tmpDir, 1), commandExecutor, ForkJoinPool.commonPool());
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

    @Test
    void testFail() throws Exception {
        Network network = createTestNetwork();
        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynawo_version.out", null);
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(tmpDir, 1), commandExecutor, ForkJoinPool.commonPool());
        DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
        assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
        DynamicSimulationResult result = dynawoSimulation.run(network, n -> Collections.emptyList(), EventModelsSupplier.empty(),
                CurvesSupplier.empty(), network.getVariantManager().getWorkingVariantId(),
                computationManager, DynamicSimulationParameters.load());
        assertNotNull(result);
        assertFalse(result.isOk());
    }

    @Test
    void testWithoutCurves() throws Exception {
        Network network = createTestNetwork();
        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynawo_version.out", "/test.xiidm");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(tmpDir, 1), commandExecutor, ForkJoinPool.commonPool());
        DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
        assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
        DynamicSimulationResult result = dynawoSimulation.run(network, n -> Collections.emptyList(), EventModelsSupplier.empty(),
                new CurvesSupplierMock(), network.getVariantManager().getWorkingVariantId(),
                computationManager, DynamicSimulationParameters.load());
        assertNotNull(result);
        assertFalse(result.isOk());
    }

    @Test
    void checkVersionCommand() {
        String versionCommand = DynaWaltzProvider.getVersionCommand(config).toString(0);
        if (SystemUtils.IS_OS_WINDOWS) {
            assertEquals("[\\home\\dynawaltz\\dynawo.cmd, version]", versionCommand);
        } else {
            assertEquals("[/home/dynawaltz/dynawo.sh, version]", versionCommand);
        }
    }

    @Test
    void checkExecutionCommand() {
        String versionCommand = DynaWaltzProvider.getCommand(config).toString(0);
        if (SystemUtils.IS_OS_WINDOWS) {
            assertEquals("[[\\home\\dynawaltz\\dynawo.cmd, jobs, powsybl_dynawaltz.jobs]]", versionCommand);
        } else {
            assertEquals("[[/home/dynawaltz/dynawo.sh, jobs, powsybl_dynawaltz.jobs]]", versionCommand);
        }
    }

    @Test
    void testCallingBadVersionDynawo() throws Exception {
        Network network = Network.create("test", "test");
        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynawo_bad_version.out", null);
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(tmpDir, 1), commandExecutor, ForkJoinPool.commonPool());
        DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
        assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
        DynamicModelsSupplier dms = n -> Collections.emptyList();
        EventModelsSupplier ems = EventModelsSupplier.empty();
        CurvesSupplier cs = CurvesSupplier.empty();
        String wvId = network.getVariantManager().getWorkingVariantId();
        DynamicSimulationParameters dsp = DynamicSimulationParameters.load();
        PowsyblException e = assertThrows(PowsyblException.class, () -> dynawoSimulation.run(network, dms, ems, cs, wvId, computationManager, dsp));
        assertEquals("dynawo version not supported. Must be >= " + DynawoConstants.VERSION_MIN, e.getMessage());
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
