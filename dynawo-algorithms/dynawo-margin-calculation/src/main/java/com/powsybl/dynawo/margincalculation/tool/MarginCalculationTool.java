/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.tool;

import com.google.auto.service.AutoService;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.io.table.*;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.dsl.GroovyDslContingenciesProviderFactory;
import com.powsybl.dynawo.contingency.results.FailedCriterion;
import com.powsybl.dynawo.contingency.results.ScenarioResult;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.DynamicSimulationSupplierFactory;
import com.powsybl.dynawo.margincalculation.MarginCalculation;
import com.powsybl.dynawo.margincalculation.MarginCalculationParameters;
import com.powsybl.dynawo.margincalculation.MarginCalculationRunParameters;
import com.powsybl.dynawo.margincalculation.json.JsonMarginCalculationParameters;
import com.powsybl.dynawo.margincalculation.json.MarginCalculationResultSerializer;
import com.powsybl.dynawo.margincalculation.loadsvariation.supplier.LoadsVariationSupplier;
import com.powsybl.dynawo.margincalculation.results.LoadIncreaseResult;
import com.powsybl.dynawo.margincalculation.results.MarginCalculationResult;
import com.powsybl.iidm.network.ImportConfig;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.tools.ConversionToolUtils;
import com.powsybl.tools.Command;
import com.powsybl.tools.Tool;
import com.powsybl.tools.ToolRunningContext;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(Tool.class)
public class MarginCalculationTool implements Tool {

    private static final String CASE_FILE = "case-file";
    private static final String DYNAMIC_MODELS_FILE = "dynamic-models-file";
    private static final String CONTINGENCIES_FILE = "contingencies-file";
    private static final String LOAD_VARIATIONS_FILE = "load-variations-file";
    private static final String PARAMETERS_FILE = "parameters-file";
    private static final String OUTPUT_FILE = "output-file";
    private static final String OUTPUT_LOG_FILE = "output-log-file";

    @Override
    public Command getCommand() {
        return new Command() {
            @Override
            public String getName() {
                return "margin-calculation";
            }

            @Override
            public String getTheme() {
                return "Computation";
            }

            @Override
            public String getDescription() {
                return "Run margin calculation";
            }

            @Override
            public Options getOptions() {
                return new Options()
                        .addOption(Option.builder().longOpt(CASE_FILE)
                                .desc("the case path")
                                .hasArg()
                                .argName("FILE")
                                .required()
                                .build())
                        .addOption(Option.builder().longOpt(DYNAMIC_MODELS_FILE)
                                .desc("dynamic models description as a Groovy file: defines the dynamic models to be associated to chosen equipments of the network")
                                .hasArg()
                                .argName("FILE")
                                .required()
                                .build())
                        .addOption(Option.builder().longOpt(CONTINGENCIES_FILE)
                                .desc("contingencies description as a Groovy file")
                                .hasArg()
                                .argName("FILE")
                                .required()
                                .build())
                        .addOption(Option.builder().longOpt(LOAD_VARIATIONS_FILE)
                                .desc("load variations description as a JSON file")
                                .hasArg()
                                .argName("FILE")
                                .required()
                                .build())
                        .addOption(Option.builder().longOpt(PARAMETERS_FILE)
                                .desc("margin calculation parameters as a JSON file")
                                .hasArg()
                                .argName("FILE")
                                .build())
                        .addOption(Option.builder().longOpt(OUTPUT_FILE)
                                .desc("margin calculation results output path")
                                .hasArg()
                                .argName("FILE")
                                .build())
                        .addOption(Option.builder().longOpt(OUTPUT_LOG_FILE)
                                .desc("margin calculation logs output path")
                                .hasArg()
                                .argName("FILE")
                                .build())
                        .addOption(ConversionToolUtils.createImportParametersFileOption())
                        .addOption(ConversionToolUtils.createImportParameterOption());
            }

            @Override
            public String getUsageFooter() {
                return null;
            }
        };
    }

    @Override
    public void run(CommandLine line, ToolRunningContext context) throws Exception {

        ReportNode reportNode = ReportNode.newRootReportNode()
                .withMessageTemplate("marginCalculationTool", "Margin Calculation Tool")
                .build();
        Path caseFile = context.getFileSystem().getPath(line.getOptionValue(CASE_FILE));

        context.getOutputStream().println("Loading network '" + caseFile + "'");
        Properties inputParams = ConversionToolUtils.readProperties(line, ConversionToolUtils.OptionType.IMPORT, context);
        Network network = Network.read(caseFile, context.getShortTimeExecutionComputationManager(), ImportConfig.load(), inputParams);
        if (network == null) {
            throw new PowsyblException("Case '" + caseFile + "' not found");
        }

        MarginCalculation.Runner runner = MarginCalculation.getRunner();
        Path dydFile = context.getFileSystem().getPath(line.getOptionValue(DYNAMIC_MODELS_FILE));
        DynamicModelsSupplier dynamicModelsSupplier = DynamicSimulationSupplierFactory.createDynamicModelsSupplier(dydFile, runner.getName());
        Path contingenciesFile = context.getFileSystem().getPath(line.getOptionValue(CONTINGENCIES_FILE));
        ContingenciesProvider contingenciesProvider = new GroovyDslContingenciesProviderFactory().create(contingenciesFile);
        Path loadVariationsFile = context.getFileSystem().getPath(line.getOptionValue(LOAD_VARIATIONS_FILE));
        LoadsVariationSupplier loadsVariationSupplier = LoadsVariationSupplier.getLoadsVariationSupplierForJson(loadVariationsFile);
        MarginCalculationParameters parameters = line.hasOption(PARAMETERS_FILE) ?
                JsonMarginCalculationParameters.read(context.getFileSystem().getPath(line.getOptionValue(PARAMETERS_FILE)))
                : MarginCalculationParameters.builder().build();
        MarginCalculationRunParameters runParameters = new MarginCalculationRunParameters()
                .setMarginCalculationParameters(parameters)
                .setComputationManager(context.getShortTimeExecutionComputationManager())
                .setReportNode(reportNode);

        MarginCalculationResult result = runner.run(network, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier, runParameters);
        //Results
        Path outputLogFile = line.hasOption(OUTPUT_LOG_FILE) ? context.getFileSystem().getPath(line.getOptionValue(OUTPUT_LOG_FILE)) : null;
        if (outputLogFile != null) {
            exportLog(reportNode, context, outputLogFile);
        } else {
            printLog(reportNode, context);
        }
        Path outputFile = line.hasOption(OUTPUT_FILE) ? context.getFileSystem().getPath(line.getOptionValue(OUTPUT_FILE)) : null;
        if (outputFile != null) {
            exportResult(result, context, outputFile);
        } else {
            printResult(result, context);
        }
    }

    private void printLog(ReportNode reportNode, ToolRunningContext context) throws IOException {
        Writer writer = new OutputStreamWriter(context.getOutputStream());
        reportNode.print(writer);
        writer.flush();
    }

    private void exportLog(ReportNode reportNode, ToolRunningContext context, Path outputLogFile) throws IOException {
        context.getOutputStream().println("Writing logs to '" + outputLogFile + "'");
        reportNode.print(outputLogFile);
    }

    private void printResult(MarginCalculationResult result, ToolRunningContext context) {
        Writer writer = new OutputStreamWriter(context.getOutputStream());
        AsciiTableFormatterFactory asciiTableFormatterFactory = new AsciiTableFormatterFactory();
        printMarginCalculationResult(result, writer, asciiTableFormatterFactory, TableFormatterConfig.load());
    }

    private void exportResult(MarginCalculationResult result, ToolRunningContext context, Path outputFile) {
        context.getOutputStream().println("Writing results to '" + outputFile + "'");
        MarginCalculationResultSerializer.write(result, outputFile);
    }

    private void printMarginCalculationResult(MarginCalculationResult results, Writer writer,
                                              TableFormatterFactory formatterFactory,
                                              TableFormatterConfig formatterConfig) {
        try (TableFormatter formatter = formatterFactory.create(writer,
                "Margin calculation results",
                formatterConfig,
                getColumns())) {
            for (LoadIncreaseResult result : results.getLoadIncreaseResults()) {
                formatter.writeCell(result.loadLevel());
                formatter.writeCell(result.status().toString());

                List<FailedCriterion> failedCriteria = result.failedCriteria();
                if (failedCriteria.isEmpty()) {
                    formatter.writeEmptyCells(2);
                } else {
                    formatter.writeCell("Failed criteria (%s)".formatted(failedCriteria.size()));
                    formatter.writeEmptyCell();
                }

                List<ScenarioResult> scenarioResults = result.scenarioResults();
                if (scenarioResults.isEmpty()) {
                    formatter.writeEmptyCells(4);
                } else {
                    formatter.writeCell("Scenarios (%s)".formatted(scenarioResults.size()));
                    formatter.writeEmptyCells(3);
                }

                for (FailedCriterion criterion : failedCriteria) {
                    formatter.writeEmptyCells(2);
                    formatter.writeCell(criterion.description());
                    formatter.writeCell(criterion.time());
                    formatter.writeEmptyCells(4);
                }

                printScenarioResult(scenarioResults, formatter);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void printScenarioResult(List<ScenarioResult> scenarioResults, TableFormatter formatter) throws IOException {
        for (ScenarioResult scenarioResult : scenarioResults) {
            formatter.writeEmptyCells(4);
            formatter.writeCell(scenarioResult.id());
            formatter.writeCell(scenarioResult.status().toString());
            List<FailedCriterion> scenarioCriteria = scenarioResult.failedCriteria();
            if (scenarioCriteria.isEmpty()) {
                formatter.writeEmptyCells(2);
            } else {
                formatter.writeCell("Scenario failed criteria (%s)".formatted(scenarioCriteria.size()));
                formatter.writeEmptyCell();
            }
            for (FailedCriterion criterion : scenarioResult.failedCriteria()) {
                formatter.writeEmptyCells(6);
                formatter.writeCell(criterion.description());
                formatter.writeCell(criterion.time());
            }
        }
    }

    private static Column[] getColumns() {
        return new Column[]{
            new Column("Load level"),
            new Column("Status"),
            new Column("Failed criteria"),
            new Column("Failed criteria time"),
            new Column("Scenarios"),
            new Column("Scenarios Status"),
            new Column("Scenarios failed criteria"),
            new Column("Scenarios failed criteria time")
        };
    }
}
