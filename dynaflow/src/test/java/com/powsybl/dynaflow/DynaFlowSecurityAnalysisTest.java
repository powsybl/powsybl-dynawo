/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.common.io.ByteStreams;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.dsl.GroovyDslContingenciesProvider;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.modification.AbstractNetworkModification;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import javax.xml.transform.Source;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static org.junit.Assert.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaFlowSecurityAnalysisTest {

    private static final String SECURITY_ANALISIS_RESULTS_FILENAME = "securityAnalysisResults.json";
    private static final String DYNAWO_PROVIDER_NAME = "DynawoSecurityAnalysis";

    private FileSystem fileSystem;
    private PlatformConfig platformConfig;

    static class DynaFlowSecurityAnalysisTestNetworkModification extends AbstractNetworkModification {
        @Override
        public void apply(Network network, boolean throwException, ComputationManager computationManager, Reporter reporter) {
            network.getLine("NHV1_NHV2_2").getTerminal1().disconnect();
            network.getLine("NHV1_NHV2_2").getTerminal2().disconnect();
            network.getLine("NHV1_NHV2_1").getTerminal2().setP(600.0);
        }
    }

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
                } else {
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
        Mockito.when(contingency.toModification()).thenReturn(new DynaFlowSecurityAnalysisTestNetworkModification());
        ContingenciesProvider contingenciesProvider = Mockito.mock(ContingenciesProvider.class);
        Mockito.when(contingenciesProvider.getContingencies(network)).thenReturn(Collections.singletonList(contingency));

        LimitViolationFilter filter = new LimitViolationFilter();

        SecurityAnalysisReport report = SecurityAnalysis.run(network, VariantManagerConstants.INITIAL_VARIANT_ID, contingenciesProvider,
                SecurityAnalysisParameters.load(platformConfig), computationManager, filter, new DefaultLimitViolationDetector(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
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

    private static class SecurityAnalysisLocalCommandExecutorMock extends AbstractLocalCommandExecutor {

        private static final Logger LOGGER = LoggerFactory.getLogger(SecurityAnalysisLocalCommandExecutorMock.class);

        private final String stdOutFileRef;

        public SecurityAnalysisLocalCommandExecutorMock(String stdoutFileRef) {
            this.stdOutFileRef = Objects.requireNonNull(stdoutFileRef);
        }

        @Override
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) throws IOException, InterruptedException {
            try {
                if (args.get(0).equals("--version")) {
                    copyFile(stdOutFileRef, errFile);
                } else {
                    validateInputs(workingDir);
                    copyOutputs(workingDir);
                }
            } catch (Throwable throwable) {
                LOGGER.error(throwable.toString(), throwable);
                return -1;
            }
            return 0;
        }

        private void validateInputs(Path workingDir) throws IOException {
            if (Files.exists(workingDir.resolve(IIDM_FILENAME))) {
                compareXml(getClass().getResourceAsStream("/SmallBusBranch/dynaflow-inputs/powsybl_dynaflow.xiidm"), Files.newInputStream(workingDir.resolve(IIDM_FILENAME)));
                compareTxt(getClass().getResourceAsStream("/SmallBusBranch/dynaflow-inputs/contingencies.json"), Files.newInputStream(workingDir.resolve("contingencies.json")));
            }
        }

        private void copyOutputs(Path workingDir) throws IOException {
            if (Files.exists(workingDir.resolve(IIDM_FILENAME))) {
                Path output = Files.createDirectories(workingDir.resolve("BaseCase").resolve("outputs").resolve("finalState").toAbsolutePath());
                copyFile("/SmallBusBranch/dynaflow-outputs/BaseCase-outputIIDM.xml", output.resolve(OUTPUT_IIDM_FILENAME));
                output = Files.createDirectories(workingDir.resolve("contingency1").resolve("outputs").resolve("finalState").toAbsolutePath());
                copyFile("/SmallBusBranch/dynaflow-outputs/contingency1-outputIIDM.xml", output.resolve(OUTPUT_IIDM_FILENAME));
            }
        }

        private static void compareXml(InputStream expected, InputStream actual) {
            Source control = Input.fromStream(expected).build();
            Source test = Input.fromStream(actual).build();
            Diff myDiff = DiffBuilder.compare(control).withTest(test).ignoreWhitespace().ignoreComments().build();
            boolean hasDiff = myDiff.hasDifferences();
            if (hasDiff) {
                System.err.println(myDiff.toString());
            }
            assertFalse(hasDiff);
        }

        private static void compareTxt(InputStream expected, InputStream actual) {
            try {
                compareTxt(expected, new String(ByteStreams.toByteArray(actual), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private static void compareTxt(InputStream expected, String actual) {
            try {
                String expectedStr = normalizeLineSeparator(new String(ByteStreams.toByteArray(expected), StandardCharsets.UTF_8));
                String actualStr = normalizeLineSeparator(actual);
                assertEquals(expectedStr, actualStr);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private static String normalizeLineSeparator(String str) {
            return str.replace("\r\n", "\n")
                    .replace("\r", "\n");
        }
    }

    @Test
    public void testSecurityAnalysisOutputs() throws IOException {
        Path workingDir = Files.createDirectory(fileSystem.getPath("SmallBusBranch"));

        // Load network
        Files.copy(getClass().getResourceAsStream("/SmallBusBranch/powsybl-inputs/SmallBusBranch.xiidm"), workingDir.resolve("network.iidm"));
        Network network = Importers.loadNetwork(workingDir.resolve("network.iidm"));

        Files.copy(getClass().getResourceAsStream("/SmallBusBranch/powsybl-inputs/contingencies.groovy"), workingDir.resolve("contingencies.groovy"));
        ContingenciesProvider contingenciesProvider = new GroovyDslContingenciesProvider(workingDir.resolve("contingencies.groovy"));

        LocalCommandExecutor commandExecutor = new SecurityAnalysisLocalCommandExecutorMock("/dynaflow_version.out");
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(workingDir, 1), commandExecutor, ForkJoinPool.commonPool());

        LimitViolationFilter filter = new LimitViolationFilter();
        SecurityAnalysisReport report = SecurityAnalysis.run(network, VariantManagerConstants.INITIAL_VARIANT_ID, contingenciesProvider,
                SecurityAnalysisParameters.load(platformConfig), computationManager, filter, new DefaultLimitViolationDetector(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        SecurityAnalysisResult result = report.getResult();

        PostContingencyResult postcontingencyResult = result.getPostContingencyResults().get(0);
        assertTrue(postcontingencyResult.getLimitViolationsResult().isComputationOk());
        assertEquals(3, postcontingencyResult.getLimitViolationsResult().getLimitViolations().size());
        LimitViolation violation = postcontingencyResult.getLimitViolationsResult().getLimitViolations().get(0);
        assertEquals(LimitViolationType.CURRENT, violation.getLimitType());
        assertEquals("_045e3524-c766-11e1-8775-005056c00008", violation.getSubjectId());
        violation = postcontingencyResult.getLimitViolationsResult().getLimitViolations().get(1);
        assertEquals(LimitViolationType.CURRENT, violation.getLimitType());
        assertEquals("_0476c63a-c766-11e1-8775-005056c00008", violation.getSubjectId());
        violation = postcontingencyResult.getLimitViolationsResult().getLimitViolations().get(2);
        assertEquals(LimitViolationType.CURRENT, violation.getLimitType());
        assertEquals("_044c81e3-c766-11e1-8775-005056c00008", violation.getSubjectId());
    }
}
