/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.ComputationResourcesStatus;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManagerConstants;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.security.*;
import com.powsybl.security.detectors.DefaultLimitViolationDetector;
import com.powsybl.security.extensions.ActivePowerExtension;
import com.powsybl.security.extensions.CurrentExtension;
import com.powsybl.security.results.PostContingencyResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import static org.junit.Assert.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaFlowSecurityAnalysisTest {

    private static final String SECURITY_ANALISIS_RESULTS_FILENAME = "securityAnalysisResults.json";
    private static final String DYNAWO_PROVIDER_NAME = "DynawoSecurityAnalysis";

    private FileSystem fileSystem;
    private PlatformConfig platformConfig;

    @Before
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    private static class LocalCommandExecutorMock extends AbstractLocalCommandExecutor {

        private final String stdOutFileRef;
        private final String outputSecurityAnalisisResult;

        public LocalCommandExecutorMock(String stdoutFileRef, String outputSecurityAnalisisResult) {
            this.stdOutFileRef = Objects.requireNonNull(stdoutFileRef);
            this.outputSecurityAnalisisResult = Objects.requireNonNull(outputSecurityAnalisisResult);
        }

        @Override
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
            try {
                if (args.get(0).equals("--version")) {
                    copyFile(stdOutFileRef, errFile);
                }  else {
                    assertEquals("--network", args.get(0));
                    assertEquals("--config", args.get(2));
                    assertEquals("--contingencies", args.get(4));
                    Files.createDirectories(workingDir.resolve("outputs"));
                    copyFile(outputSecurityAnalisisResult, workingDir.resolve("outputs").resolve(SECURITY_ANALISIS_RESULTS_FILENAME));
                }
                return 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Test
    public void testDefaultProvider() {
        SecurityAnalysis.Runner dynawoSecurityAnalysisRunner = SecurityAnalysis.find();
        assertEquals(DYNAWO_PROVIDER_NAME, dynawoSecurityAnalysisRunner.getName());
        assertEquals("1.0", dynawoSecurityAnalysisRunner.getVersion());
    }

    @Test
    public void test() throws IOException {
        Network network = EurostagTutorialExample1Factory.create();
        ((Bus) network.getIdentifiable("NHV1")).setV(380.0);
        ((Bus) network.getIdentifiable("NHV2")).setV(380.0);
        network.getLine("NHV1_NHV2_1").getTerminal1().setP(560.0).setQ(550.0);
        network.getLine("NHV1_NHV2_1").getTerminal2().setP(560.0).setQ(550.0);
        network.getLine("NHV1_NHV2_1").newCurrentLimits1().setPermanentLimit(1500.0).add();
        network.getLine("NHV1_NHV2_1").newCurrentLimits2()
                .setPermanentLimit(1200.0)
                .beginTemporaryLimit()
                .setName("10'")
                .setAcceptableDuration(10 * 60)
                .setValue(1300.0)
                .endTemporaryLimit()
                .add();

        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynaflow_version.out",
                "/security_analisis_result.json");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());

        Contingency contingency = Contingency.builder("NHV1_NHV2_2_contingency")
                .addBranch("NHV1_NHV2_2")
                .build();
        contingency = Mockito.spy(contingency);
        Mockito.when(contingency.toTask()).thenReturn((network1, computationManager1) -> {
            network1.getLine("NHV1_NHV2_2").getTerminal1().disconnect();
            network1.getLine("NHV1_NHV2_2").getTerminal2().disconnect();
            network1.getLine("NHV1_NHV2_1").getTerminal2().setP(600.0);
        });
        ContingenciesProvider contingenciesProvider = Mockito.mock(ContingenciesProvider.class);
        Mockito.when(contingenciesProvider.getContingencies(network)).thenReturn(Collections.singletonList(contingency));

        LimitViolationFilter filter = new LimitViolationFilter();

        SecurityAnalysisReport report = SecurityAnalysis.run(network, VariantManagerConstants.INITIAL_VARIANT_ID, new DefaultLimitViolationDetector(), filter, computationManager, SecurityAnalysisParameters.load(platformConfig), contingenciesProvider, Collections.emptyList());
        SecurityAnalysisResult result = report.getResult();

        assertTrue(result.getPreContingencyResult().getLimitViolationsResult().isComputationOk());
        assertEquals(1, result.getPreContingencyResult().getLimitViolationsResult().getLimitViolations().size());
        PostContingencyResult postcontingencyResult = result.getPostContingencyResults().get(0);
        assertTrue(postcontingencyResult.getLimitViolationsResult().isComputationOk());
        assertEquals(3, postcontingencyResult.getLimitViolationsResult().getLimitViolations().size());
        LimitViolation violation = postcontingencyResult.getLimitViolationsResult().getLimitViolations().get(0);
        assertEquals(LimitViolationType.CURRENT, violation.getLimitType());
        assertEquals("NHV1_NHV2_2", violation.getSubjectId());

        ActivePowerExtension extension1 = violation.getExtension(ActivePowerExtension.class);
        assertNotNull(extension1);
        assertEquals(220.0, extension1.getPreContingencyValue(), 0.0);
        assertEquals(230.0, extension1.getPostContingencyValue(), 0.0);

        CurrentExtension extension2 = violation.getExtension(CurrentExtension.class);
        assertNotNull(extension2);
        assertEquals(95.0, extension2.getPreContingencyValue(), 0.0);
    }

    private static ComputationManager createMockComputationManager() {
        ComputationManager computationManager = Mockito.mock(ComputationManager.class);
        Executor executor = Runnable::run;
        Mockito.when(computationManager.getExecutor()).thenReturn(executor);
        ComputationResourcesStatus computationResourcesStatus = Mockito.mock(ComputationResourcesStatus.class);
        Mockito.when(computationResourcesStatus.getAvailableCores()).thenReturn(4);
        Mockito.when(computationManager.getResourcesStatus()).thenReturn(computationResourcesStatus);
        return computationManager;
    }
}
