/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.it;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.computation.ComputationManager;
import com.powsybl.dynamicsimulation.tool.DynamicSimulationTool;
import com.powsybl.dynamicsimulation.tool.ListDynamicSimulationModelsTool;
import com.powsybl.dynawo.margincalculation.tool.MarginCalculationTool;
import com.powsybl.loadflow.tools.RunLoadFlowTool;
import com.powsybl.security.dynamic.tools.DynamicSecurityAnalysisTool;
import com.powsybl.security.tools.SecurityAnalysisTool;
import com.powsybl.tools.CommandLineTools;
import com.powsybl.tools.ToolInitializationContext;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class IToolsTest extends AbstractDynawoTest {

    private static CommandLineTools TOOLS;
    private ToolInitializationContext toolContext;
    private FileSystem fileSystem;
    private ByteArrayOutputStream bout;
    private ByteArrayOutputStream berr;

    @BeforeAll
    static void beforeAll() {
        TOOLS = new CommandLineTools(List.of(
                new RunLoadFlowTool(),
                new SecurityAnalysisTool(),
                new DynamicSimulationTool(),
                new ListDynamicSimulationModelsTool(),
                new DynamicSecurityAnalysisTool(),
                new MarginCalculationTool()));
    }

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Files.copy(getResourceAsStream("/ieee14/IEEE14.iidm"), fileSystem.getPath("IEEE14.iidm"));
        Files.copy(getResourceAsStream("/ieee14/dynamicModels.groovy"), fileSystem.getPath("dynamicModels.groovy"));
        Files.copy(getResourceAsStream("/itools/eventModels.groovy"), fileSystem.getPath("eventModels.groovy"));
        Files.copy(getResourceAsStream("/ieee14/contingencies.groovy"), fileSystem.getPath("contingencies.groovy"));
        Files.copy(getResourceAsStream("/itools/outputVariables.groovy"), fileSystem.getPath("outputVariables.groovy"));
        Files.copy(getResourceAsStream("/ieee14/loadsVariations.json"), fileSystem.getPath("loadsVariations.json"));
        Files.copy(getResourceAsStream("/itools/MarginCalculationParameters.json"), fileSystem.getPath("MarginCalculationParameters.json"));

        bout = new ByteArrayOutputStream();
        berr = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bout);
        PrintStream err = new PrintStream(berr);
        toolContext = new ToolInitializationContext() {

            @Override
            public PrintStream getOutputStream() {
                return out;
            }

            @Override
            public PrintStream getErrorStream() {
                return err;
            }

            @Override
            public FileSystem getFileSystem() {
                return fileSystem;
            }

            @Override
            public Options getAdditionalOptions() {
                return new Options();
            }

            @Override
            public ComputationManager createShortTimeExecutionComputationManager(CommandLine commandLine) {
                return computationManager;
            }

            @Override
            public ComputationManager createLongTimeExecutionComputationManager(CommandLine commandLine) {
                return computationManager;
            }
        };
    }

    @AfterEach
    void tearDown() {
        super.tearDown();
        try {
            fileSystem.close();
            bout.close();
            berr.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLF() throws IOException {
        String[] args = {"loadflow", "--case-file", "IEEE14.iidm"};
        int status = TOOLS.run(args, toolContext);

        assertEquals(CommandLineTools.COMMAND_OK_STATUS, status);
        String expectedOutput = new String(getResourceAsStream("/itools/lf_out.txt").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expectedOutput, getOutput());
        assertThat(getError()).isEmpty();
    }

    @Test
    void testSA() throws IOException {
        String[] args = {"security-analysis", "--case-file", "IEEE14.iidm",
            "--contingencies-file", "contingencies.groovy"};
        int status = TOOLS.run(args, toolContext);

        assertEquals(CommandLineTools.COMMAND_OK_STATUS, status);
        String expectedOutput = new String(getResourceAsStream("/itools/sa_out.txt").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expectedOutput, getOutput());
        assertThat(getError()).isEmpty();
    }

    @Test
    void testDynaSim() throws IOException {
        String[] args = {"dynamic-simulation", "--case-file", "IEEE14.iidm",
            "--dynamic-models-file", "dynamicModels.groovy"};
        int status = TOOLS.run(args, toolContext);

        assertEquals(CommandLineTools.COMMAND_OK_STATUS, status);
        String expectedOutput = new String(getResourceAsStream("/itools/dyna_sim_out.txt").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expectedOutput, getOutput());
        assertThat(getError()).isEmpty();
    }

    @Test
    void testDynaSimEventOutputVariables() throws IOException {
        String[] args = {"dynamic-simulation", "--case-file", "IEEE14.iidm",
            "--dynamic-models-file", "dynamicModels.groovy",
            "--event-models-file", "eventModels.groovy",
            "--output-variables-file", "outputVariables.groovy"};
        int status = TOOLS.run(args, toolContext);

        assertEquals(CommandLineTools.COMMAND_OK_STATUS, status);
        String expectedOutput = new String(getResourceAsStream("/itools/dyna_sim_event_ov_out.txt").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expectedOutput, getOutput());
        assertThat(getError()).isEmpty();
    }

    @Test
    void testDynamicModelsList() throws IOException {
        String[] args = {"list-dynamic-simulation-models"};
        int status = TOOLS.run(args, toolContext);

        assertEquals(CommandLineTools.COMMAND_OK_STATUS, status);
        String expectedOutput = new String(getResourceAsStream("/itools/list_models_out.txt").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expectedOutput, getOutput());
        assertThat(getError()).isEmpty();
    }

    @Test
    void testDSA() throws IOException {
        String[] args = {"dynamic-security-analysis", "--case-file", "IEEE14.iidm",
            "--dynamic-models-file", "dynamicModels.groovy",
            "--contingencies-file", "contingencies.groovy"};
        int status = TOOLS.run(args, toolContext);

        assertEquals(CommandLineTools.COMMAND_OK_STATUS, status);
        String expectedOutput = new String(getResourceAsStream("/itools/dsa_out.txt").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expectedOutput, getOutput());
        assertThat(getError()).isEmpty();
    }

    @Test
    void testDSAWithEvent() throws IOException {
        String[] args = {"dynamic-security-analysis", "--case-file", "IEEE14.iidm",
            "--dynamic-models-file", "dynamicModels.groovy",
            "--event-models-file", "eventModels.groovy",
            "--contingencies-file", "contingencies.groovy"};
        int status = TOOLS.run(args, toolContext);

        assertEquals(CommandLineTools.COMMAND_OK_STATUS, status);
        String expectedOutput = new String(getResourceAsStream("/itools/dsa_event_out.txt").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expectedOutput, getOutput());
        assertThat(getError()).isEmpty();
    }

    @Test
    void testMC() throws IOException {
        String[] args = {"margin-calculation", "--case-file", "IEEE14.iidm",
            "--dynamic-models-file", "dynamicModels.groovy",
            "--contingencies-file", "contingencies.groovy",
            "--load-variations-file", "loadsVariations.json"};
        int status = TOOLS.run(args, toolContext);

        assertEquals(CommandLineTools.COMMAND_OK_STATUS, status);
        String expectedOutput = new String(getResourceAsStream("/itools/mc_out.txt").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expectedOutput, getOutput());
        assertThat(getError()).isEmpty();
    }

    @Test
    void testMCJsonParameters() throws IOException {
        String[] args = {"margin-calculation", "--case-file", "IEEE14.iidm",
            "--dynamic-models-file", "dynamicModels.groovy",
            "--contingencies-file", "contingencies.groovy",
            "--load-variations-file", "loadsVariations.json",
            "--parameters-file", "MarginCalculationParameters.json"};
        int status = TOOLS.run(args, toolContext);

        assertEquals(CommandLineTools.COMMAND_OK_STATUS, status);
        String expectedOutput = new String(getResourceAsStream("/itools/mc_out.txt").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expectedOutput, getOutput());
        assertThat(getError()).isEmpty();
    }

    private String getOutput() {
        return bout.toString(StandardCharsets.UTF_8);
    }

    private String getError() {
        return berr.toString(StandardCharsets.UTF_8);
    }
}
