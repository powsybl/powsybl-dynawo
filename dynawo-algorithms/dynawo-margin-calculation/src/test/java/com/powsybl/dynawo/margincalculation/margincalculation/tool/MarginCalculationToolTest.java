/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.margincalculation.tool;

import com.powsybl.commons.test.ComparisonUtils;
import com.powsybl.dynawo.contingency.results.FailedCriterion;
import com.powsybl.dynawo.contingency.results.ScenarioResult;
import com.powsybl.dynawo.contingency.results.Status;
import com.powsybl.dynawo.margincalculation.MarginCalculationProvider;
import com.powsybl.dynawo.margincalculation.results.LoadIncreaseResult;
import com.powsybl.dynawo.margincalculation.results.MarginCalculationResult;
import com.powsybl.dynawo.margincalculation.tool.MarginCalculationTool;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class MarginCalculationToolTest extends AbstractToolTest {

    private static final MockedConstruction.MockInitializer<MarginCalculationProvider> MOCK_INITIALIZER =
            (mock, context) -> {
                when(mock.getName()).thenReturn("Mock");
                when(mock.run(any(), any(), any(), any(), any(), any()))
                        .thenReturn(CompletableFuture.completedFuture(new MarginCalculationResult(List.of(
                                new LoadIncreaseResult(100, Status.CRITERIA_NON_RESPECTED,
                                        Collections.emptyList(),
                                        List.of(new FailedCriterion("failed", 10),
                                                new FailedCriterion("failed2", 20))),
                                new LoadIncreaseResult(50, Status.DIVERGENCE,
                                        List.of(new ScenarioResult("Disconnect line", Status.CRITERIA_NON_RESPECTED,
                                                        List.of(new FailedCriterion("Sc failed", 10),
                                                                new FailedCriterion("Sc failed2", 20))),
                                                new ScenarioResult("Disconnect gen", Status.CONVERGENCE))),
                                new LoadIncreaseResult(25, Status.CONVERGENCE)
                        ))));
            };

    private final MarginCalculationTool tool = new MarginCalculationTool();

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("/empty_network.xiidm")), fileSystem.getPath("/network.xiidm"));
        Files.createFile(fileSystem.getPath("/dynamicModels.groovy"));
        Files.createFile(fileSystem.getPath("/contingencies.groovy"));
        Files.createFile(fileSystem.getPath("/loadsVariations.json"));
    }

    @Override
    protected Iterable<Tool> getTools() {
        return Collections.singleton(tool);
    }

    @Override
    @Test
    public void assertCommand() {
        Command command = tool.getCommand();
        assertCommand(command, "margin-calculation", 9, 4);
        assertEquals("Computation", command.getTheme());
        assertEquals("Run margin calculation", command.getDescription());
        assertNull(command.getUsageFooter());
        assertOption(command.getOptions(), "case-file", true, true);
        assertOption(command.getOptions(), "dynamic-models-file", true, true);
        assertOption(command.getOptions(), "contingencies-file", true, true);
        assertOption(command.getOptions(), "load-variations-file", true, true);
        assertOption(command.getOptions(), "parameters-file", false, true);
        assertOption(command.getOptions(), "output-file", false, true);
        assertOption(command.getOptions(), "output-log-file", false, true);
        assertOption(command.getOptions(), "import-parameters", false, true);
        assertOption(command.getOptions(), "I", false, true);
    }

    @Test
    void testMarginCalculation() {
        try (MockedConstruction<MarginCalculationProvider> provider = Mockito.mockConstruction(MarginCalculationProvider.class, MOCK_INITIALIZER)) {

            String expectedOut = """
                    Loading network '/network.xiidm'
                    Margin Calculation Tool
                    Margin calculation results:
                    +------------+------------------------+---------------------+----------------------+-----------------+------------------------+------------------------------+--------------------------------+
                    | Load level | Status                 | Failed criteria     | Failed criteria time | Scenarios       | Scenarios Status       | Scenarios failed criteria    | Scenarios failed criteria time |
                    +------------+------------------------+---------------------+----------------------+-----------------+------------------------+------------------------------+--------------------------------+
                    | 100,000    | CRITERIA_NON_RESPECTED | Failed criteria (2) |                      |                 |                        |                              |                                |
                    |            |                        | failed              | 10,0000              |                 |                        |                              |                                |
                    |            |                        | failed2             | 20,0000              |                 |                        |                              |                                |
                    | 50,0000    | DIVERGENCE             |                     |                      | Scenarios (2)   |                        |                              |                                |
                    |            |                        |                     |                      | Disconnect line | CRITERIA_NON_RESPECTED | Scenario failed criteria (2) |                                |
                    |            |                        |                     |                      |                 |                        | Sc failed                    | 10,0000                        |
                    |            |                        |                     |                      |                 |                        | Sc failed2                   | 20,0000                        |
                    |            |                        |                     |                      | Disconnect gen  | CONVERGENCE            |                              |                                |
                    | 25,0000    | CONVERGENCE            |                     |                      |                 |                        |                              |                                |
                    +------------+------------------------+---------------------+----------------------+-----------------+------------------------+------------------------------+--------------------------------+
                    """;
            assertCommandSuccessful(new String[]{"margin-calculation",
                "--case-file", "/network.xiidm",
                "--dynamic-models-file", "/dynamicModels.groovy",
                "--contingencies-file", "/contingencies.groovy",
                "--load-variations-file", "/loadsVariations.groovy"}, expectedOut);
        }
    }

    @Test
    void testMarginCalculationWithOutputFile() throws IOException {
        try (MockedConstruction<MarginCalculationProvider> provider = Mockito.mockConstruction(MarginCalculationProvider.class, MOCK_INITIALIZER)) {

            String expectedOut = """
                    Loading network '/network.xiidm'
                    Margin Calculation Tool
                    Writing results to 'outputTest.json'
                    """;
            String expectedOutputFile = """
                    {
                      "version" : "1.0",
                      "loadIncreases" : [ {
                        "loadLevel" : 100.0,
                        "status" : "CRITERIA_NON_RESPECTED",
                        "failedCriteria" : [ {
                          "description" : "failed",
                          "time" : 10.0
                        }, {
                          "description" : "failed2",
                          "time" : 20.0
                        } ],
                        "scenarioResults" : [ ]
                      }, {
                        "loadLevel" : 50.0,
                        "status" : "DIVERGENCE",
                        "failedCriteria" : [ ],
                        "scenarioResults" : [ {
                          "id" : "Disconnect line",
                          "status" : "CRITERIA_NON_RESPECTED",
                          "failedCriteria" : [ {
                            "description" : "Sc failed",
                            "time" : 10.0
                          }, {
                            "description" : "Sc failed2",
                            "time" : 20.0
                          } ]
                        }, {
                          "id" : "Disconnect gen",
                          "status" : "CONVERGENCE",
                          "failedCriteria" : [ ]
                        } ]
                      }, {
                        "loadLevel" : 25.0,
                        "status" : "CONVERGENCE",
                        "failedCriteria" : [ ],
                        "scenarioResults" : [ ]
                      } ]
                    }""";
            assertCommandSuccessful(new String[]{"margin-calculation",
                "--case-file", "/network.xiidm",
                "--dynamic-models-file", "/dynamicModels.groovy",
                "--contingencies-file", "/contingencies.groovy",
                "--load-variations-file", "/loadsVariations.groovy",
                "--output-file", "outputTest.json"}, expectedOut);
            ComparisonUtils.assertTxtEquals(expectedOutputFile, Files.newInputStream(fileSystem.getPath("outputTest.json")));
        }
    }

    @Test
    void testMarginCalculationWithLogFile() throws IOException {
        try (MockedConstruction<MarginCalculationProvider> provider = Mockito.mockConstruction(MarginCalculationProvider.class, MOCK_INITIALIZER)) {

            String expectedOut = """
                    Loading network '/network.xiidm'
                    Writing logs to 'outputTest.log'
                    Margin calculation results:
                    +------------+------------------------+---------------------+----------------------+-----------------+------------------------+------------------------------+--------------------------------+
                    | Load level | Status                 | Failed criteria     | Failed criteria time | Scenarios       | Scenarios Status       | Scenarios failed criteria    | Scenarios failed criteria time |
                    +------------+------------------------+---------------------+----------------------+-----------------+------------------------+------------------------------+--------------------------------+
                    | 100,000    | CRITERIA_NON_RESPECTED | Failed criteria (2) |                      |                 |                        |                              |                                |
                    |            |                        | failed              | 10,0000              |                 |                        |                              |                                |
                    |            |                        | failed2             | 20,0000              |                 |                        |                              |                                |
                    | 50,0000    | DIVERGENCE             |                     |                      | Scenarios (2)   |                        |                              |                                |
                    |            |                        |                     |                      | Disconnect line | CRITERIA_NON_RESPECTED | Scenario failed criteria (2) |                                |
                    |            |                        |                     |                      |                 |                        | Sc failed                    | 10,0000                        |
                    |            |                        |                     |                      |                 |                        | Sc failed2                   | 20,0000                        |
                    |            |                        |                     |                      | Disconnect gen  | CONVERGENCE            |                              |                                |
                    | 25,0000    | CONVERGENCE            |                     |                      |                 |                        |                              |                                |
                    +------------+------------------------+---------------------+----------------------+-----------------+------------------------+------------------------------+--------------------------------+
                    """;
            String expectedOutputFile = "Margin Calculation Tool\n";
            assertCommandSuccessful(new String[]{"margin-calculation",
                "--case-file", "/network.xiidm",
                "--dynamic-models-file", "/dynamicModels.groovy",
                "--contingencies-file", "/contingencies.groovy",
                "--load-variations-file", "/loadsVariations.groovy",
                "--output-log-file", "outputTest.log"}, expectedOut);
            ComparisonUtils.assertTxtEquals(expectedOutputFile, Files.newInputStream(fileSystem.getPath("outputTest.log")));
        }
    }

    @Test
    void testWrongNetwork() {
        try (MockedConstruction<MarginCalculationProvider> provider = Mockito.mockConstruction(MarginCalculationProvider.class, MOCK_INITIALIZER)) {
            assertCommandErrorMatch(new String[]{"margin-calculation",
                "--case-file", "/wrong_network.xiidm",
                "--dynamic-models-file", "/dynamicModels.groovy",
                "--contingencies-file", "/contingencies.groovy",
                "--load-variations-file", "/loadsVariations.groovy"},
                CommandLineTools.EXECUTION_ERROR_STATUS,
                "File /wrong_network.xiidm does not exist");
        }
    }

    @Override
    protected void assertCommandSuccessful(String[] args, String expectedOut) {
        assertCommandResult(args, CommandLineTools.COMMAND_OK_STATUS, expectedOut, "",
                (s1, s2) -> assertThat(s2.trim()).isEqualToIgnoringNewLines(s1.trim()));
    }
}
