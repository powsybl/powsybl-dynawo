/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.test.AbstractConverterTest;
import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.security.SecurityAnalysis;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisReport;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.json.SecurityAnalysisResultSerializer;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import static com.powsybl.commons.test.ComparisonUtils.compareTxt;
import static com.powsybl.commons.test.ComparisonUtils.compareXml;
import static com.powsybl.dynaflow.DynaFlowConstants.DYNAFLOW_NAME;
import static com.powsybl.dynaflow.DynaFlowConstants.IIDM_FILENAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynaFlowSecurityAnalysisTest extends AbstractConverterTest {

    private static class LocalCommandExecutorMock extends AbstractLocalCommandExecutor {

        private final String stdOutFileRef;
        private final String inputFile;
        private final String contingencyFile;
        private final List<String> contingencyIds;
        private final List<String> constraints;

        public LocalCommandExecutorMock(String stdoutFileRef, String inputFile) {
            this(stdoutFileRef, inputFile, null, List.of(), List.of());
        }

        public LocalCommandExecutorMock(String stdoutFileRef, String inputFile, String contingencyFile, List<String> contingencyIds, List<String> outputConstraints) {
            this.stdOutFileRef = Objects.requireNonNull(stdoutFileRef);
            this.inputFile = inputFile;
            this.contingencyFile = contingencyFile;
            this.contingencyIds = contingencyIds;
            this.constraints = outputConstraints;
        }

        @Override
        public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
            try {
                if (args.get(0).equals("--version")) {
                    copyFile(stdOutFileRef, errFile);
                } else {
                    assertEquals("--network network.xiidm --config config.json --contingencies contingencies.json", String.join(" ", args));
                    validateInputs(workingDir);
                    copyOutputs(workingDir);
                }
                return 0;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private void validateInputs(Path workingDir) throws IOException {
            if (inputFile != null) {
                compareXml(getClass().getResourceAsStream(inputFile), Files.newInputStream(workingDir.resolve(IIDM_FILENAME)));
            }
            if (contingencyFile != null) {
                InputStream contingencyIs = Objects.requireNonNull(getClass().getResourceAsStream(contingencyFile));
                compareTxt(contingencyIs, Files.newInputStream(workingDir.resolve("contingencies.json")));
            }
        }

        private void copyOutputs(Path workingDir) throws IOException {
            Path constraintsFolder = Files.createDirectories(workingDir.resolve("constraints"));
            for (int i = 0; i < contingencyIds.size(); i++) {
                copyFile(constraints.get(i), constraintsFolder.resolve("constraints_" + contingencyIds.get(i) + ".xml"));
            }
        }
    }

    @Test
    void testDefaultProvider() {
        SecurityAnalysis.Runner dynawoSecurityAnalysisRunner = SecurityAnalysis.find();
        assertEquals(DYNAFLOW_NAME, dynawoSecurityAnalysisRunner.getName());
    }

    @Test
    void test() throws IOException {
        Network network = buildNetwork();

        Contingency contingency1 = Contingency.builder("NHV1_NHV2_2_contingency").addBranch("NHV1_NHV2_2").build();
        Contingency contingency2 = Contingency.builder("NB_NGEN_contingency").addBranch("NB_NGEN").build();
        List<Contingency> contingencies = List.of(contingency1, contingency2);
        List<String> contingencyIds = contingencies.stream().map(Contingency::getId).collect(Collectors.toList());

        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynawo_version.out",
                "/SecurityAnalysis/input.xiidm", "/SecurityAnalysis/contingencies.json",
                contingencyIds, List.of("/SecurityAnalysis/constraints1.xml", "/SecurityAnalysis/constraints2.xml"));
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());

        SecurityAnalysisReport report = SecurityAnalysis.run(network, n -> contingencies, SecurityAnalysisParameters.load(), computationManager);
        SecurityAnalysisResult result = report.getResult();

        StringWriter writer = new StringWriter();
        SecurityAnalysisResultSerializer.write(result, writer);
        compareTxt(Objects.requireNonNull(getClass().getResourceAsStream("/SecurityAnalysis/result.json")), writer.toString());
    }

    @Test
    void testCallingBadVersionDynawo() throws IOException {
        Network network = Network.create("test", "test");
        ContingenciesProvider contingenciesProvider = n -> List.of();
        LocalCommandExecutor commandExecutor = new LocalCommandExecutorMock("/dynawo_bad_version.out", null);
        ComputationManager computationManager = new LocalComputationManager(new LocalComputationConfig(fileSystem.getPath("/working-dir"), 1), commandExecutor, ForkJoinPool.commonPool());
        SecurityAnalysisParameters sap = SecurityAnalysisParameters.load();
        assertThrows(PowsyblException.class, () -> SecurityAnalysis.run(network, contingenciesProvider, sap, computationManager));
    }

    private static Network buildNetwork() {
        Network network = EurostagTutorialExample1Factory.create();
        network.setCaseDate(DateTime.parse("2023-03-23T16:40:48.060+01:00"));

        // Changing the network for having some pre-contingencies violations
        network.getBusBreakerView().getBus("NHV1").setV(380.0);
        network.getBusBreakerView().getBus("NHV2").setV(380.0);
        Line line = network.getLine("NHV1_NHV2_1");
        line.getTerminal1().setP(560.0).setQ(550.0);
        line.getTerminal2().setP(560.0).setQ(550.0);

        // Adding strong current limits to have some post-contingencies current limit violations
        line.newCurrentLimits1().setPermanentLimit(40.0).add();
        line.newCurrentLimits2()
                .beginTemporaryLimit().setName("10'").setAcceptableDuration(10 * 60).setValue(450.0).endTemporaryLimit()
                .setPermanentLimit(1000)
                .add();

        // Adding a node breaker voltage level to the network
        Substation sNb = network.newSubstation().setId("NB_S").add();
        VoltageLevel vlNb = sNb.newVoltageLevel().setId("NB_VL").setTopologyKind(TopologyKind.NODE_BREAKER)
                .setNominalV(400).setHighVoltageLimit(405).setLowVoltageLimit(395).add();
        vlNb.getNodeBreakerView().newBusbarSection().setId("NB_BBS").setNode(0).add();
        vlNb.getNodeBreakerView().newBreaker().setId("NB_BG").setNode1(0).setNode2(1).setRetained(true).add();
        vlNb.getNodeBreakerView().newDisconnector().setId("NB_DL").setNode1(0).setNode2(2).add();
        vlNb.newGenerator().setId("NB_GEN").setNode(1).setTargetP(8).setTargetV(390).setMinP(0).setMaxP(11).setVoltageRegulatorOn(true).add();
        Line lineNbBb = network.newLine().setId("NB_NGEN").setVoltageLevel1(vlNb.getId()).setNode1(2).setVoltageLevel2("VLGEN").setBus2("NGEN")
                .setR(3.0).setX(33.0).setB1(193E-6).setB2(193E-6).add();
        lineNbBb.newCurrentLimits1().setPermanentLimit(41).add();

        return network;
    }
}
