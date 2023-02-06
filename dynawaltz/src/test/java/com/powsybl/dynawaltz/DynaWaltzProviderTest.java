/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.test.AbstractConverterTest;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.dynamicsimulation.*;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Before;
import org.junit.Test;

import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.iidm.network.Network;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.JOBS_FILENAME;
import static org.junit.Assert.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzProviderTest extends AbstractConverterTest {

    private static final String OUTPUT_IIDM_FILENAME = "outputIIDM.xml";
    private final String extension = SystemUtils.IS_OS_WINDOWS ? ".cmd" : ".sh";
    private Path homeDir;
    private DynaWaltzConfig config;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        homeDir = fileSystem.getPath("/home/dynawaltz");
        config = DynaWaltzConfig.load();
    }

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

        private final String stdOutFileRef;

        public EmptyLocalCommandExecutorMock(String stdoutFileRef) {
            this.stdOutFileRef = Objects.requireNonNull(stdoutFileRef);
        }

        @Override
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
            try {
                copyFile(stdOutFileRef, outFile);
                Files.createDirectories(workingDir.resolve("outputs").resolve("finalState"));
                return 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static class WithoutCurvesLocalCommandExecutorMock extends AbstractLocalCommandExecutor {

        private final String stdOutFileRef;
        private final String outputIidm;

        public WithoutCurvesLocalCommandExecutorMock(String stdOutFileRef, String outputIidm) {
            this.stdOutFileRef = stdOutFileRef;
            this.outputIidm = Objects.requireNonNull(outputIidm);
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
    public void checkVersionCommand() {
        String program = homeDir.resolve("dynawo" + extension).toString();
        if (SystemUtils.IS_OS_WINDOWS) {
            program = program.replace("/", "\\");
        }
        String versionCommand = DynaWaltzProvider.getVersionCommand(config).toString(0);
        String expectedVersionCommand = "[" + program + ", version]";
        assertEquals(expectedVersionCommand, versionCommand);
    }

    @Test
    public void checkExecutionCommand() {
        String program = homeDir.resolve("dynawo" + extension).toString();
        if (SystemUtils.IS_OS_WINDOWS) {
            program = program.replace("/", "\\");
        }
        String versionCommand = DynaWaltzProvider.getCommand(config).toString(0);
        String expectedVersionCommand = "[[" + program + ", jobs, " + JOBS_FILENAME + "]]";
        assertEquals(expectedVersionCommand, versionCommand);
    }

    @Test
    public void testWithoutCurves() throws Exception {
        Network network = Network.create("test", "test");
        LocalCommandExecutor commandExecutor = new WithoutCurvesLocalCommandExecutorMock("/dynawo_version.out", "/test.xiidm");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(tmpDir, 1), commandExecutor, ForkJoinPool.commonPool());
        DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
        assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
        DynamicSimulationResult result = dynawoSimulation.run(network, DynamicModelsSupplierMock.empty(), EventModelsSupplierMock.empty(),
                new CurvesSupplierMock(), network.getVariantManager().getWorkingVariantId(),
                computationManager, DynamicSimulationParameters.load());
        assertNotNull(result);
        assertFalse(result.isOk());
    }

    @Test
    public void testFail() throws Exception {
        Network network = Network.create("test", "test");
        LocalCommandExecutor commandExecutor = new EmptyLocalCommandExecutorMock("/dynawo_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(tmpDir, 1), commandExecutor, ForkJoinPool.commonPool());
        DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
        assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
        DynamicSimulationResult result = dynawoSimulation.run(network, DynamicModelsSupplierMock.empty(), EventModelsSupplierMock.empty(),
                CurvesSupplier.empty(), network.getVariantManager().getWorkingVariantId(),
                computationManager, DynamicSimulationParameters.load());
        assertNotNull(result);
        assertFalse(result.isOk());
    }

    @Test
    public void testCallingBadVersionDynawo() throws Exception {
        Network network = Network.create("test", "test");
        LocalCommandExecutor commandExecutor = new EmptyLocalCommandExecutorMock("/dynawo_bad_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(tmpDir, 1), commandExecutor, ForkJoinPool.commonPool());
        DynamicSimulation.Runner dynawoSimulation = DynamicSimulation.find();
        assertEquals(DynaWaltzProvider.NAME, dynawoSimulation.getName());
        assertThrows(PowsyblException.class, () -> dynawoSimulation.run(network, DynamicModelsSupplierMock.empty(), EventModelsSupplierMock.empty(),
                CurvesSupplier.empty(), network.getVariantManager().getWorkingVariantId(),
                computationManager, DynamicSimulationParameters.load()));
    }
}
