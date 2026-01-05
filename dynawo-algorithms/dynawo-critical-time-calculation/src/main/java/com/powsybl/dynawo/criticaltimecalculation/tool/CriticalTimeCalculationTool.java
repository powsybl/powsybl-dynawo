/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.tool;

import com.google.auto.service.AutoService;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.io.table.*;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.DynamicSimulationSupplierFactory;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculation;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationParameters;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationRunParameters;
import com.powsybl.dynawo.criticaltimecalculation.json.CriticalTimeCalculationResultsSerializer;
import com.powsybl.dynawo.criticaltimecalculation.json.JsonCriticalTimeCalculationParameters;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultsProvider;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResult;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResults;
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
import java.util.Properties;

import static com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationReports.createCriticalTimeCalculationReportNode;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
@AutoService(Tool.class)
public class CriticalTimeCalculationTool implements Tool {

    private static final String CASE_FILE = "case-file";
    private static final String DYNAMIC_MODELS_FILE = "dynamic-models-file";
    private static final String NODE_FAULTS_FILE = "node-faults-file";
    private static final String PARAMETERS_FILE = "parameters-file";
    private static final String OUTPUT_FILE = "output-file";
    private static final String OUTPUT_LOG_FILE = "output-log-file";

    @Override
    public Command getCommand() {
        return new Command() {
            @Override
            public String getName() {
                return "critical-time-calculation";
            }

            @Override
            public String getTheme() {
                return "Computation";
            }

            @Override
            public String getDescription() {
                return "Run critical time calculation";
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
                        .addOption(Option.builder().longOpt(NODE_FAULTS_FILE)
                                .desc("node faults description as a JSON file")
                                .hasArg()
                                .argName("FILE")
                                .required()
                                .build())
                        .addOption(Option.builder().longOpt(PARAMETERS_FILE)
                                .desc("critical time calculation parameters as a JSON file")
                                .hasArg()
                                .argName("FILE")
                                .build())
                        .addOption(Option.builder().longOpt(OUTPUT_FILE)
                                .desc("critical time calculation results output path")
                                .hasArg()
                                .argName("FILE")
                                .build())
                        .addOption(Option.builder().longOpt(OUTPUT_LOG_FILE)
                                .desc("critical time calculation logs output path")
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

        ReportNode reportNode = createCriticalTimeCalculationReportNode();
        Path caseFile = context.getFileSystem().getPath(line.getOptionValue(CASE_FILE));

        context.getOutputStream().println("Loading network '" + caseFile + "'");
        Properties inputParams = ConversionToolUtils.readProperties(line, ConversionToolUtils.OptionType.IMPORT, context);
        Network network = Network.read(caseFile, context.getShortTimeExecutionComputationManager(), ImportConfig.load(), inputParams);
        if (network == null) {
            throw new PowsyblException("Case '" + caseFile + "' not found");
        }

        CriticalTimeCalculation.Runner runner = CriticalTimeCalculation.getRunner();
        Path dydFile = context.getFileSystem().getPath(line.getOptionValue(DYNAMIC_MODELS_FILE));
        DynamicModelsSupplier dynamicModelsSupplier = DynamicSimulationSupplierFactory.createDynamicModelsSupplier(dydFile, runner.getName());
        Path nodeFaultsFile = context.getFileSystem().getPath(line.getOptionValue(NODE_FAULTS_FILE));
        NodeFaultsProvider nodeFaultsProvider = NodeFaultsProvider.getNodeFaultsProviderForJson(nodeFaultsFile);
        CriticalTimeCalculationParameters parameters = line.hasOption(PARAMETERS_FILE) ?
                JsonCriticalTimeCalculationParameters.read(context.getFileSystem().getPath(line.getOptionValue(PARAMETERS_FILE)))
                : CriticalTimeCalculationParameters.load();
        CriticalTimeCalculationRunParameters runParameters = new CriticalTimeCalculationRunParameters()
                .setCriticalTimeCalculationParameters(parameters)
                .setComputationManager(context.getShortTimeExecutionComputationManager())
                .setReportNode(reportNode);

        CriticalTimeCalculationResults results = runner.run(network, dynamicModelsSupplier, nodeFaultsProvider, runParameters);
        //Results
        Path outputLogFile = line.hasOption(OUTPUT_LOG_FILE) ? context.getFileSystem().getPath(line.getOptionValue(OUTPUT_LOG_FILE)) : null;
        if (outputLogFile != null) {
            exportLog(reportNode, context, outputLogFile);
        } else {
            printLog(reportNode, context);
        }
        Path outputFile = line.hasOption(OUTPUT_FILE) ? context.getFileSystem().getPath(line.getOptionValue(OUTPUT_FILE)) : null;
        if (outputFile != null) {
            exportResult(results, context, outputFile);
        } else {
            printResult(results, context);
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

    private void printResult(CriticalTimeCalculationResults results, ToolRunningContext context) {
        Writer writer = new OutputStreamWriter(context.getOutputStream());
        AsciiTableFormatterFactory asciiTableFormatterFactory = new AsciiTableFormatterFactory();
        printCriticalTimeCalculationResults(results, writer, asciiTableFormatterFactory, TableFormatterConfig.load());
    }

    private void exportResult(CriticalTimeCalculationResults results, ToolRunningContext context, Path outputFile) {
        context.getOutputStream().println("Writing results to '" + outputFile + "'");
        CriticalTimeCalculationResultsSerializer.write(results, outputFile);
    }

    private void printCriticalTimeCalculationResults(CriticalTimeCalculationResults results, Writer writer,
                                              TableFormatterFactory formatterFactory,
                                              TableFormatterConfig formatterConfig) {
        try (TableFormatter formatter = formatterFactory.create(writer,
                "Critical Time Calculation results",
                formatterConfig,
                getColumns())) {
            for (CriticalTimeCalculationResult result : results.criticalTimeCalculationResults()) {
                formatter.writeCell(result.id());
                formatter.writeCell(result.status().toString());
                formatter.writeCell(result.criticalTime());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Column[] getColumns() {
        return new Column[]{
            new Column("Id"),
            new Column("Status"),
            new Column("Critical Time"),
        };
    }
}
