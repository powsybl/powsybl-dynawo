/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.dynamicsimulation.DynamicSimulationResult;
import com.powsybl.dynamicsimulation.DynamicSimulationResultImpl;
import com.powsybl.dynamicsimulation.TimelineEvent;
import com.powsybl.dynawo.commons.ExportMode;
import com.powsybl.dynawo.outputvariables.CsvFsvParser;
import com.powsybl.dynawo.commons.CommonReports;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.NetworkResultsUpdater;
import com.powsybl.dynawo.commons.dynawologs.CsvLogParser;
import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.dynawo.commons.timeline.TimeLineParser;
import com.powsybl.dynawo.xml.JobsXml;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.serde.NetworkSerDe;
import com.powsybl.timeseries.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.powsybl.dynawo.DynawoFilesUtils.*;
import static com.powsybl.dynawo.DynawoSimulationConstants.*;
import static com.powsybl.dynawo.commons.DynawoConstants.*;
import static com.powsybl.dynawo.commons.DynawoUtil.getCommandExecutions;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynawoSimulationHandler extends AbstractExecutionHandler<DynamicSimulationResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoSimulationHandler.class);
    private static final String OUTPUT_DUMP_FILENAME = "outputState.dmp";
    private static final String ERROR_FILENAME = "dyn_fs_0.err";
    private static final String DYNAWO_ERROR_PATTERN = "DYN Error: ";

    private final DynawoSimulationContext context;
    private final Command command;
    private final Network dynawoInput;
    private final ReportNode reportNode;

    private final List<TimelineEvent> timeline = new ArrayList<>();
    private final Map<String, DoubleTimeSeries> curves = new LinkedHashMap<>();
    private final Map<String, Double> fsv = new LinkedHashMap<>();
    private DynamicSimulationResult.Status status = DynamicSimulationResult.Status.SUCCESS;
    private String statusText = "";

    public DynawoSimulationHandler(DynawoSimulationContext context, Command command, ReportNode reportNode) {
        this.context = context;
        this.dynawoInput = context.getDynawoSimulationParameters().isMergeLoads()
                ? LoadsMerger.mergeLoads(context.getNetwork())
                : context.getNetwork();
        this.command = command;
        this.reportNode = reportNode;
    }

    @Override
    public List<CommandExecution> before(Path workingDir) throws IOException {
        Path basePath = workingDir.resolve(OUTPUTS_FOLDER);
        deleteExistingFile(basePath, FINAL_STATE_FOLDER, OUTPUT_IIDM_FILENAME);
        deleteExistingFile(basePath, CURVES_OUTPUT_PATH, CURVES_FILENAME);
        deleteExistingFile(basePath, FSV_OUTPUT_PATH, FSV_OUTPUT_FILENAME);
        writeInputFiles(workingDir);
        return getCommandExecutions(command);
    }

    @Override
    public DynamicSimulationResult after(Path workingDir, ExecutionReport report) throws IOException {

        Path outputsFolder = workingDir.resolve(OUTPUTS_FOLDER);
        context.getNetwork().getVariantManager().setWorkingVariant(context.getWorkingVariantId());
        setDynawoLog(outputsFolder, context.getDynawoSimulationParameters().getSpecificLogs());
        setTimeline(outputsFolder);
        if (context.withCurveVariables()) {
            setCurves(outputsFolder);
        }
        if (context.withFsvVariables()) {
            setFinalStateValues(outputsFolder);
        }
        Path errorFile = workingDir.resolve(ERROR_FILENAME);
        if (Files.exists(errorFile)) {
            Matcher errorMatcher = Pattern.compile(DYNAWO_ERROR_PATTERN + "(.*)").matcher(Files.readString(errorFile));
            if (!errorMatcher.find()) {
                setSuccessOutputs(workingDir, outputsFolder);
            } else {
                status = DynamicSimulationResult.Status.FAILURE;
                statusText = errorMatcher.group().substring(DYNAWO_ERROR_PATTERN.length());
            }
        } else {
            LOGGER.warn("Error file not found");
            status = DynamicSimulationResult.Status.FAILURE;
            statusText = "Dynawo error log file not found";
        }
        return new DynamicSimulationResultImpl(status, statusText, curves, fsv, timeline);
    }

    private void setSuccessOutputs(Path workingDir, Path outputsFolder) throws IOException {
        updateNetwork(workingDir);
        DumpFileParameters dumpFileParameters = context.getDynawoSimulationParameters().getDumpFileParameters();
        if (dumpFileParameters.exportDumpFile()) {
            setDumpFile(outputsFolder, dumpFileParameters.dumpFileFolder(), workingDir.getFileName());
        }
    }

    private void setDynawoLog(Path outputsFolder, Set<DynawoSimulationParameters.SpecificLog> specificLogs) throws IOException {
        Path logFolder = outputsFolder.resolve(LOGS_FOLDER);
        if (Files.exists(logFolder)) {
            Path logFile = logFolder.resolve(LOGS_FILENAME);
            if (Files.exists(logFile)) {
                ReportNode logReportNode = CommonReports.createDynawoLogReportNode(reportNode);
                new CsvLogParser().parse(logFile).forEach(e -> CommonReports.reportLogEntry(logReportNode, e));
            }
            for (DynawoSimulationParameters.SpecificLog specificLog : specificLogs) {
                Path specificLogFile = logFolder.resolve(specificLog.getFileName());
                if (Files.exists(specificLogFile)) {
                    ReportNode logReport = DynawoSimulationReports.createDynawoSpecificLogReportNode(reportNode, specificLog);
                    DynawoSimulationReports.reportSpecificLogEntry(logReport, Files.readString(specificLogFile));
                }
            }
        } else {
            LOGGER.warn("Dynawo logs file not found");
        }
    }

    private void updateNetwork(Path workDir) {
        Path outputNetworkFile = workDir.resolve(OUTPUT_IIDM_FILENAME_PATH);
        if (Files.exists(outputNetworkFile)) {
            NetworkResultsUpdater.update(context.getNetwork(), NetworkSerDe.read(outputNetworkFile), context.getDynawoSimulationParameters().isMergeLoads());
        } else {
            LOGGER.warn("Output IIDM file not found");
            status = DynamicSimulationResult.Status.FAILURE;
            statusText = "Dynawo Output IIDM file not found";
        }
    }

    private void setDumpFile(Path outputsFolder, Path dumpFileFolder, Path fileName) throws IOException {
        Path outputDumpFile = outputsFolder.resolve(FINAL_STATE_FOLDER).resolve(OUTPUT_DUMP_FILENAME);
        if (Files.exists(outputDumpFile)) {
            Files.copy(outputDumpFile, dumpFileFolder.resolve(fileName + "_" + OUTPUT_DUMP_FILENAME), StandardCopyOption.REPLACE_EXISTING);
        } else {
            LOGGER.warn("Dump file {} not found, export will be skipped", OUTPUT_DUMP_FILENAME);
        }
    }

    private void setTimeline(Path outputsFolder) {
        ExportMode exportMode = context.getDynawoSimulationParameters().getTimelineExportMode();
        Path timelineFile = outputsFolder.resolve(TIMELINE_FOLDER).resolve(TIMELINE_FILENAME + exportMode.getFileExtension());
        if (Files.exists(timelineFile)) {
            TimeLineParser.parse(timelineFile, exportMode)
                    .forEach(e -> timeline.add(new TimelineEvent(e.time(), e.modelName(), e.message())));
        } else {
            LOGGER.warn("Timeline file not found");
        }
    }

    private void setCurves(Path workingDir) {
        Path curvesPath = workingDir.resolve(CURVES_OUTPUT_PATH).resolve(CURVES_FILENAME);
        if (Files.exists(curvesPath)) {
            TimeSeries.parseCsv(curvesPath, new TimeSeriesCsvConfig(TimeSeriesConstants.DEFAULT_SEPARATOR, false,
                            TimeSeries.TimeFormat.FRACTIONS_OF_SECOND, true, true))
                    .values().forEach(l -> l.forEach(curve -> curves.put(curve.getMetadata().getName(), (DoubleTimeSeries) curve)));
        } else {
            LOGGER.warn("Curves folder not found");
            status = DynamicSimulationResult.Status.FAILURE;
            statusText = "Dynawo curves folder not found";
        }
    }

    private void setFinalStateValues(Path workingDir) {
        Path fsvPath = workingDir.resolve(FSV_OUTPUT_PATH).resolve(FSV_OUTPUT_FILENAME);
        if (Files.exists(fsvPath)) {
            new CsvFsvParser(';').parse(fsvPath).forEach(e -> fsv.put(e.model() + "_" + e.variable(), e.value()));
        } else {
            LOGGER.warn("Final state values folder not found");
            status = DynamicSimulationResult.Status.FAILURE;
            statusText = "Dynawo final state values folder not found";
        }
    }

    private void writeInputFiles(Path workingDir) throws IOException {
        DynawoUtil.writeIidm(dynawoInput, workingDir.resolve(NETWORK_FILENAME));
        JobsXml.write(workingDir, context);
        DynawoFilesUtils.writeInputFiles(workingDir, context);
    }
}
