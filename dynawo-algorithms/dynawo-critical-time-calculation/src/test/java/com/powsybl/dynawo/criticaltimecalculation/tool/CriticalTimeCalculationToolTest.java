/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.tool;

import com.powsybl.commons.test.ComparisonUtils;
import com.powsybl.dynawo.contingency.results.Status;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationProvider;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResult;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResults;
import com.powsybl.tools.Command;
import com.powsybl.tools.CommandLineTools;
import com.powsybl.tools.Tool;
import com.powsybl.tools.test.AbstractToolTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationToolTest extends AbstractToolTest {

    private static final MockedConstruction.MockInitializer<CriticalTimeCalculationProvider> MOCK_INITIALIZER =
            (mock, context) -> {
                when(mock.getName()).thenReturn("Mock");
                when(mock.run(any(), any(), any(), any(), any()))
                        .thenReturn(CompletableFuture.completedFuture(new CriticalTimeCalculationResults(List.of(
                                new CriticalTimeCalculationResult("NodeFault_0", Status.RESULT_FOUND, 1),
                                new CriticalTimeCalculationResult("NodeFault_1", Status.CT_BELOW_MIN_BOUND),
                                new CriticalTimeCalculationResult("NodeFault_2", Status.CT_ABOVE_MAX_BOUND)
                        ))));
            };

    private final CriticalTimeCalculationTool tool = new CriticalTimeCalculationTool();

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("/empty_network.xiidm")), fileSystem.getPath("/network.xiidm"));
        Files.createFile(fileSystem.getPath("/dynamicModels.groovy"));
        Files.createFile(fileSystem.getPath("/nodeFaults.json"));
    }

    @Override
    protected Iterable<Tool> getTools() {
        return Collections.singleton(tool);
    }

    @Override
    @Test
    public void assertCommand() {
        Command command = tool.getCommand();
        assertCommand(command, "critical-time-calculation", 8, 3);
        assertEquals("Computation", command.getTheme());
        assertEquals("Run critical time calculation", command.getDescription());
        assertNull(command.getUsageFooter());
        assertOption(command.getOptions(), "case-file", true, true);
        assertOption(command.getOptions(), "dynamic-models-file", true, true);
        assertOption(command.getOptions(), "node-faults-file", true, true);
        assertOption(command.getOptions(), "parameters-file", false, true);
        assertOption(command.getOptions(), "output-file", false, true);
        assertOption(command.getOptions(), "output-log-file", false, true);
        assertOption(command.getOptions(), "import-parameters", false, true);
        assertOption(command.getOptions(), "I", false, true);
    }

    @Test
    void testCriticalTimeCalculation() {
        try (MockedConstruction<CriticalTimeCalculationProvider> provider = Mockito.mockConstruction(CriticalTimeCalculationProvider.class, MOCK_INITIALIZER)) {

            String expectedOut = """
                    Loading network '/network.xiidm'
                    Critical Time Calculation Tool
                    Critical Time Calculation results:
                    +-------------+--------------------+---------------+
                    | Id          | Status             | Critical Time |
                    +-------------+--------------------+---------------+
                    | NodeFault_0 | RESULT_FOUND       | 1,00000       |
                    | NodeFault_1 | CT_BELOW_MIN_BOUND | inv           |
                    | NodeFault_2 | CT_ABOVE_MAX_BOUND | inv           |
                    +-------------+--------------------+---------------+
                    """;
            assertCommandSuccessful(new String[]{"critical-time-calculation",
                "--case-file", "/network.xiidm",
                "--dynamic-models-file", "/dynamicModels.groovy",
                "--node-faults-file", "/nodeFaults.json"}, expectedOut);
        }
    }

    @Test
    void testCriticalTimeCalculationWithOutputFile() throws IOException {
        try (MockedConstruction<CriticalTimeCalculationProvider> provider = Mockito.mockConstruction(CriticalTimeCalculationProvider.class, MOCK_INITIALIZER)) {

            String expectedOut = """
                    Loading network '/network.xiidm'
                    Critical Time Calculation Tool
                    Writing results to 'outputTest.json'
                    """;
            assertCommandSuccessful(new String[]{"critical-time-calculation",
                "--case-file", "/network.xiidm",
                "--dynamic-models-file", "/dynamicModels.groovy",
                "--node-faults-file", "/nodeFaults.json",
                "--output-file", "outputTest.json"}, expectedOut);
            ComparisonUtils.assertTxtEquals(Objects.requireNonNull(getClass().getResourceAsStream("/tool_result.json")),
                    Files.newInputStream(fileSystem.getPath("outputTest.json")));
        }
    }

    @Test
    void testCriticalTimeCalculationWithLogFile() throws IOException {
        try (MockedConstruction<CriticalTimeCalculationProvider> provider = Mockito.mockConstruction(CriticalTimeCalculationProvider.class, MOCK_INITIALIZER)) {

            String expectedOut = """
                    Loading network '/network.xiidm'
                    Writing logs to 'outputTest.log'
                    Critical Time Calculation results:
                    +-------------+--------------------+---------------+
                    | Id          | Status             | Critical Time |
                    +-------------+--------------------+---------------+
                    | NodeFault_0 | RESULT_FOUND       | 1,00000       |
                    | NodeFault_1 | CT_BELOW_MIN_BOUND | inv           |
                    | NodeFault_2 | CT_ABOVE_MAX_BOUND | inv           |
                    +-------------+--------------------+---------------+
                    """;
            String expectedOutputFile = "Critical Time Calculation Tool\n";
            assertCommandSuccessful(new String[]{"critical-time-calculation",
                "--case-file", "/network.xiidm",
                "--dynamic-models-file", "/dynamicModels.groovy",
                "--node-faults-file", "/nodeFaults.json",
                "--output-log-file", "outputTest.log"}, expectedOut);
            ComparisonUtils.assertTxtEquals(expectedOutputFile, Files.newInputStream(fileSystem.getPath("outputTest.log")));
        }
    }

    @Test
    void testWrongNetwork() {
        try (MockedConstruction<CriticalTimeCalculationProvider> provider = Mockito.mockConstruction(CriticalTimeCalculationProvider.class, MOCK_INITIALIZER)) {
            assertCommandErrorMatch(new String[]{"critical-time-calculation",
                "--case-file", "/wrong_network.xiidm",
                "--dynamic-models-file", "/dynamicModels.groovy",
                "--node-faults-file", "/nodeFaults.json"},
                CommandLineTools.EXECUTION_ERROR_STATUS,
                "File /wrong_network.xiidm does not exist");
        }
    }
}
